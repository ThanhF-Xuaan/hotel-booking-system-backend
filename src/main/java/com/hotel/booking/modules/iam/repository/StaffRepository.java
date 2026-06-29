package com.hotel.booking.modules.iam.repository;

import com.hotel.booking.modules.iam.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
    List<Staff> findAllByIsDeletedFalse();

    @Query("SELECT s FROM Staff s WHERE s.isDeleted = false")
    List<Staff> findAllActiveStaffs();

    @Query("SELECT s FROM Staff s WHERE s.id = :id AND s.isDeleted = false")
    Optional<Staff> findActiveStaffById(@Param("id") Integer id);

    boolean existsByUsernameAndIsDeletedFalse(String username);

    Optional<Staff> findByUsernameAndIsDeletedFalse(String username);
}
