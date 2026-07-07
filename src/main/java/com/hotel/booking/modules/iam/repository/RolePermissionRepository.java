package com.hotel.booking.modules.iam.repository;

import com.hotel.booking.modules.iam.entity.RolePermission;
import com.hotel.booking.modules.iam.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
    List<RolePermission> findAllByRoleId(Short roleId);
    void deleteAllByIdInBatch(Iterable<RolePermissionId> ids);
}
