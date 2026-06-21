package com.hotel.booking.modules.pos.repository;

import com.hotel.booking.modules.pos.entity.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
    List<ServiceOrder> findAllByIsDeletedFalse();
}
