package com.hotel.booking.modules.inventory.service;

import java.time.LocalDate;

public interface RoomMaintenanceService {
    void scheduleMaintenance(Integer roomInstanceId, LocalDate startDate, LocalDate endDate);
}
