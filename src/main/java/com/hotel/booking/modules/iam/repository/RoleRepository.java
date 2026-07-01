package com.hotel.booking.modules.iam.repository;

import com.hotel.booking.modules.iam.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Short> {
    List<Role> findAllByIsDeletedFalse();
    Optional<Role> findByIdAndIsDeletedFalse(Short id);
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Short id);
    Optional<Role> findByCodeAndIsDeletedFalse(String code);
}
