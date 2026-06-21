package com.hotel.booking.modules.pos.repository;

import com.hotel.booking.modules.pos.entity.ServiceOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceOrderDetailRepository extends JpaRepository<ServiceOrderDetail, Long> {
}
