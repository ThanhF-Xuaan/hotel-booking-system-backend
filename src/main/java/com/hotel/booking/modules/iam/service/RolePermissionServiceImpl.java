package com.hotel.booking.modules.iam.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.iam.dto.request.RolePermissionSyncRequest;
import com.hotel.booking.modules.iam.dto.response.AssignedPermissionResponse;
import com.hotel.booking.modules.iam.entity.Permission;
import com.hotel.booking.modules.iam.entity.Role;
import com.hotel.booking.modules.iam.entity.RolePermission;
import com.hotel.booking.modules.iam.entity.RolePermissionId;
import com.hotel.booking.modules.iam.repository.PermissionRepository;
import com.hotel.booking.modules.iam.repository.RolePermissionRepository;
import com.hotel.booking.modules.iam.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class RolePermissionServiceImpl implements RolePermissionService {

    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public List<AssignedPermissionResponse> syncRolePermissions(Short roleId, RolePermissionSyncRequest request) {
        log.info("Synchronizing role permissions for roleId: {}", roleId);

        // 1. Validate Role exists and is not deleted
        Role role = roleRepository.findByIdAndIsDeletedFalse(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        // 2. Validate all requested permissionIds exist and are not soft-deleted
        Set<Short> requestedPermissionIds = request.getPermissionIds();
        List<Permission> validPermissions = permissionRepository.findAllById(requestedPermissionIds);

        boolean anyInvalid = validPermissions.size() != requestedPermissionIds.size()
                || validPermissions.stream().anyMatch(p -> Boolean.TRUE.equals(p.getIsDeleted()));
        if (anyInvalid) {
            throw new AppException(ErrorCode.PERMISSION_NOT_FOUND);
        }

        Map<Short, Permission> permissionMap = validPermissions.stream()
                .collect(Collectors.toMap(Permission::getId, Function.identity()));

        // 3. Fetch existing RolePermissions
        List<RolePermission> existingRolePermissions = rolePermissionRepository.findAllByRoleId(roleId);
        Set<Short> existingPermissionIds = existingRolePermissions.stream()
                .map(rp -> rp.getPermission().getId())
                .collect(Collectors.toSet());

        // 4. Compute differences
        Set<Short> toDeleteIds = new HashSet<>(existingPermissionIds);
        toDeleteIds.removeAll(requestedPermissionIds);

        Set<Short> toInsertIds = new HashSet<>(requestedPermissionIds);
        toInsertIds.removeAll(existingPermissionIds);

        // 5. Execute deletions
        if (!toDeleteIds.isEmpty()) {
            List<RolePermissionId> toDeleteRolePermissionIds = toDeleteIds.stream()
                    .map(pid -> new RolePermissionId(roleId, pid))
                    .toList();
            rolePermissionRepository.deleteAllByIdInBatch(toDeleteRolePermissionIds);
            log.info("Deleted {} removed permissions for roleId: {}", toDeleteRolePermissionIds.size(), roleId);
        }

        // 6. Execute insertions
        if (!toInsertIds.isEmpty()) {
            List<RolePermission> toInsert = toInsertIds.stream()
                    .map(pid -> {
                        Permission p = permissionMap.get(pid);
                        RolePermissionId rpId = new RolePermissionId(roleId, pid);
                        return new RolePermission(rpId, role, p);
                    })
                    .toList();
            rolePermissionRepository.saveAll(toInsert);
            log.info("Inserted {} new permissions for roleId: {}", toInsert.size(), roleId);
        }

        // 7. Map updated state to response list
        return validPermissions.stream()
                .map(p -> AssignedPermissionResponse.builder()
                        .permissionId(p.getId())
                        .name(p.getAction().toUpperCase() + " " + p.getResource().toUpperCase())
                        .code(p.getAction().toUpperCase() + "_" + p.getResource().toUpperCase())
                        .build())
                .toList();
    }
}
