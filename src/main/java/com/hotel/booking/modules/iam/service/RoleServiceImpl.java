package com.hotel.booking.modules.iam.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.iam.dto.request.RoleCreationRequest;
import com.hotel.booking.modules.iam.dto.request.RoleUpdateRequest;
import com.hotel.booking.modules.iam.dto.response.RoleResponse;
import com.hotel.booking.modules.iam.entity.Role;
import com.hotel.booking.modules.iam.mapper.RoleMapper;
import com.hotel.booking.modules.iam.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    RoleRepository roleRepository;
    RoleMapper roleMapper;

    @Override
    public List<RoleResponse> getAllRoles() {
        log.info("Fetching all active roles");
        return roleRepository.findAllByIsDeletedFalse()
                .stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    @Override
    public RoleResponse getRoleById(Short id) {
        log.info("Fetching role with id: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        return roleMapper.toResponse(role);
    }

    @Override
    @Transactional
    public RoleResponse createRole(RoleCreationRequest request) {
        log.info("Creating new role with code: {}", request.getCode());

        if (roleRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.ROLE_CODE_EXISTED);
        }

        Role role = roleMapper.toEntity(request);
        role.setIsDeleted(false);
        Role savedRole = roleRepository.save(role);

        log.info("Role created successfully with id: {}", savedRole.getId());
        return roleMapper.toResponse(savedRole);
    }

    @Override
    @Transactional
    public RoleResponse updateRole(Short id, RoleUpdateRequest request) {
        log.info("Updating role with id: {}", id);

        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        if (roleRepository.existsByCodeAndIdNot(request.getCode(), id)) {
            throw new AppException(ErrorCode.ROLE_CODE_EXISTED);
        }

        roleMapper.updateEntity(request, existingRole);
        Role savedRole = roleRepository.save(existingRole);

        log.info("Role updated successfully with id: {}", savedRole.getId());
        return roleMapper.toResponse(savedRole);
    }

    @Override
    @Transactional
    public void deleteRole(Short id) {
        log.info("Soft-deleting role with id: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        role.setIsDeleted(true);
        roleRepository.save(role);

        log.info("Role soft-deleted successfully with id: {}", id);
    }
}
