package com.hotel.booking.modules.iam.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.iam.dto.request.PermissionCreationRequest;
import com.hotel.booking.modules.iam.dto.request.PermissionUpdateRequest;
import com.hotel.booking.modules.iam.dto.response.PermissionResponse;
import com.hotel.booking.modules.iam.entity.Permission;
import com.hotel.booking.modules.iam.mapper.PermissionMapper;
import com.hotel.booking.modules.iam.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
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
public class PermissionServiceImpl implements PermissionService {

    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @Override
    @Transactional
    public PermissionResponse createPermission(PermissionCreationRequest request) {
        log.info("Creating permission with action: {} and resource: {}", request.getAction(), request.getResource());
        if (permissionRepository.existsByActionAndResource(request.getAction(), request.getResource())) {
            throw new AppException(ErrorCode.PERMISSION_ALREADY_EXISTS);
        }
        Permission permission = permissionMapper.toEntity(request);
        permission.setIsDeleted(false);
        Permission savedPermission = permissionRepository.save(permission);
        log.info("Permission created successfully with id: {}", savedPermission.getId());
        return permissionMapper.toResponse(savedPermission);
    }

    @Override
    public List<PermissionResponse> getAllPermissions() {
        log.info("Fetching all active permissions");
        return permissionRepository.findAllByIsDeletedFalse()
                .stream()
                .map(permissionMapper::toResponse)
                .toList();
    }

    @Override
    public PermissionResponse getPermissionById(Short id) {
        log.info("Fetching permission with id: {}", id);
        Permission permission = permissionRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));
        return permissionMapper.toResponse(permission);
    }

    @Override
    @Transactional
    public PermissionResponse updatePermission(Short id, PermissionUpdateRequest request) {
        log.info("Updating permission status for id: {}", id);
        Permission permission = permissionRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));
        permissionMapper.updateEntity(request, permission);
        Permission savedPermission = permissionRepository.save(permission);
        log.info("Permission updated successfully for id: {}", id);
        return permissionMapper.toResponse(savedPermission);
    }

    @Override
    @Transactional
    public void deletePermission(Short id) {
        log.info("Soft-deleting permission with id: {}", id);
        Permission permission = permissionRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));
        permission.setIsDeleted(true);
        permissionRepository.save(permission);
        log.info("Permission soft-deleted successfully with id: {}", id);
    }
}
