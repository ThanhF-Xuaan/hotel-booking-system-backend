package com.hotel.booking.modules.iam.service;

import com.hotel.booking.modules.iam.dto.request.StaffCreationRequest;
import com.hotel.booking.modules.iam.dto.request.StaffUpdateRequest;
import com.hotel.booking.modules.iam.dto.response.StaffResponse;

import java.util.List;

public interface StaffService {
    StaffResponse createStaff(StaffCreationRequest request);
    List<StaffResponse> getAllStaffs();
    StaffResponse getStaffById(Integer id);
    StaffResponse updateStaff(Integer id, StaffUpdateRequest request);
    void deleteStaff(Integer id);
}
