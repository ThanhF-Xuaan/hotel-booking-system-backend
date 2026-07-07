package com.hotel.booking.modules.iam.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.iam.dto.request.AuthenticationRequest;
import com.hotel.booking.modules.iam.dto.request.IntrospectRequest;
import com.hotel.booking.modules.iam.dto.response.AuthenticationResponse;
import com.hotel.booking.modules.iam.dto.response.IntrospectResponse;
import com.hotel.booking.modules.iam.entity.Staff;
import com.hotel.booking.modules.iam.repository.StaffRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    StaffRepository staffRepository;
    PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;


    @Override
    public IntrospectResponse introspect(IntrospectRequest introspectRequest)
            throws JOSEException, ParseException {
        try {
            var token = introspectRequest.getToken();

            JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY.getBytes());

            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            var verified = signedJWT.verify(jwsVerifier);


            return IntrospectResponse.builder()
                    .valid(verified && expiryTime.after(new Date()))
                    .build();
        }catch (ParseException parseException){
            log.warn("Invalid JWT format");
            return IntrospectResponse.builder()
                    .valid(false)
                    .build();
        }
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        var staff = staffRepository.findByUsernameAndIsDeletedFalse(authenticationRequest.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));

        boolean authenticate = passwordEncoder.matches(authenticationRequest.getPassword(), staff.getPassword());

        if(!authenticate){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(staff);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    private String generateToken(Staff staff){
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(staff.getFullName())
                .issuer("hotel.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("roles", staff.getRole().getCode())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();

        }catch (JOSEException joseException){
            log.error("Can't create token", joseException);
            throw new RuntimeException(joseException);
        }
    }
}
