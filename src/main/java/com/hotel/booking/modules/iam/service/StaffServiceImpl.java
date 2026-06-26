package com.hotel.booking.modules.iam.service;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.iam.dto.request.StaffCreationRequest;
import com.hotel.booking.modules.iam.dto.request.StaffUpdateRequest;
import com.hotel.booking.modules.iam.dto.response.StaffResponse;
import com.hotel.booking.modules.iam.entity.Role;
import com.hotel.booking.modules.iam.entity.Staff;
import com.hotel.booking.modules.iam.mapper.StaffMapper;
import com.hotel.booking.modules.iam.repository.RoleRepository;
import com.hotel.booking.modules.iam.repository.StaffRepository;
import com.hotel.booking.modules.inventory.entity.Hotel;
import com.hotel.booking.modules.inventory.repository.HotelRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class StaffServiceImpl implements StaffService {

    StaffRepository staffRepository;
    StaffMapper staffMapper;
    HotelRepository hotelRepository;
    RoleRepository roleRepository;

    @Override
    @Transactional
    public StaffResponse createStaff(StaffCreationRequest request) {
        log.info("Creating new staff member with username: {}", request.getUsername());

        if (staffRepository.existsByUsernameAndIsDeletedFalse(request.getUsername())) {
            throw new AppException(ErrorCode.STAFF_USERNAME_EXISTED);
        }

        Hotel hotel = null;
        if (request.getHotelId() != null) {
            hotel = hotelRepository.findByIdAndIsDeletedFalse(request.getHotelId())
                    .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
        }

        Role role = null;
        if (request.getRoleId() != null) {
            role = roleRepository.findByIdAndIsDeletedFalse(request.getRoleId())
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        }

        Staff staff = staffMapper.toEntity(request);
        staff.setHotel(hotel);
        staff.setRole(role);

        // Concatenate firstName and lastName to fullName
        String firstName = request.getFirstName() != null ? request.getFirstName().trim() : "";
        String lastName = request.getLastName() != null ? request.getLastName().trim() : "";
        staff.setFullName((firstName + " " + lastName).trim());

        if (staff.getStatus() == null) {
            staff.setStatus(ActiveStatus.ACTIVE);
        }
        staff.setIsDeleted(false);

        Staff savedStaff = staffRepository.save(staff);
        log.info("Staff member created successfully with id: {}", savedStaff.getId());
        return staffMapper.toResponse(savedStaff);
    }

    @Override
    public List<StaffResponse> getAllStaffs() {
        log.info("Fetching all active staff members");
        return staffRepository.findAllActiveStaffs()
                .stream()
                .map(staffMapper::toResponse)
                .toList();
    }

    @Override
    public StaffResponse getStaffById(Integer id) {
        log.info("Fetching active staff member with id: {}", id);
        Staff staff = staffRepository.findActiveStaffById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
        return staffMapper.toResponse(staff);
    }

    @Override
    @Transactional
    public StaffResponse updateStaff(Integer id, StaffUpdateRequest request) {
        log.info("Updating staff member with id: {}", id);

        Staff staff = staffRepository.findActiveStaffById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));

        if (request.getRoleId() != null) {
            Role role = roleRepository.findByIdAndIsDeletedFalse(request.getRoleId())
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            staff.setRole(role);
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            staff.setPassword(request.getPassword());
        }

        staffMapper.updateEntity(request, staff);

        // Recompute fullName
        String firstName = request.getFirstName() != null ? request.getFirstName().trim() : (staff.getFirstName() != null ? staff.getFirstName().trim() : "");
        String lastName = request.getLastName() != null ? request.getLastName().trim() : (staff.getLastName() != null ? staff.getLastName().trim() : "");
        staff.setFullName((firstName + " " + lastName).trim());

        Staff savedStaff = staffRepository.save(staff);
        log.info("Staff member updated successfully with id: {}", savedStaff.getId());
        return staffMapper.toResponse(savedStaff);
    }

    @Override
    @Transactional
    public void deleteStaff(Integer id) {
        log.info("Soft-deleting staff member with id: {}", id);

        Staff staff = staffRepository.findActiveStaffById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));

        staff.setIsDeleted(true);
        staffRepository.save(staff);
        log.info("Staff member soft-deleted successfully with id: {}", id);
    }
}
