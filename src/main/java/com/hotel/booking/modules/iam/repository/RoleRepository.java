package com.hotel.booking.modules.iam.repository;

import com.hotel.booking.modules.iam.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Short> {
    List<Role> findAllByIsDeletedFalse();
}
