package com.hotel.booking.modules.iam.repository;

import com.hotel.booking.modules.iam.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
    List<Staff> findAllByIsDeletedFalse();
}
