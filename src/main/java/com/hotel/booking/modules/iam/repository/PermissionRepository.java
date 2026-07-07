package com.hotel.booking.modules.iam.repository;

import com.hotel.booking.modules.iam.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Short> {
    List<Permission> findAllByIsDeletedFalse();

    boolean existsByActionAndResource(String action, String resource);

    Optional<Permission> findByIdAndIsDeletedFalse(Short id);
}

