package com.hotel.booking.modules.iam.service;

import com.hotel.booking.modules.iam.dto.request.AuthenticationRequest;
import com.hotel.booking.modules.iam.dto.request.IntrospectRequest;
import com.hotel.booking.modules.iam.dto.response.AuthenticationResponse;
import com.hotel.booking.modules.iam.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationService {
    IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException;
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);
}
