package com.hotel.booking.core.config;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.modules.iam.entity.Staff;
import com.hotel.booking.modules.iam.repository.StaffRepository;
import com.hotel.booking.modules.iam.repository.RoleRepository;
import com.hotel.booking.modules.inventory.repository.HotelRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class ApplicationInitConfig {

    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_PASSWORD:admin123}")
    String adminPassword;

    @Value("${DEFAULT_STAFF_PASSWORD:admin123}")
    String staffPassword;

    @Bean
    @Transactional
    CommandLineRunner initData(StaffRepository staffRepository,
                               RoleRepository roleRepository,
                               HotelRepository hotelRepository) {
        return args -> {
            // Seed Admin
            if (staffRepository.findByUsernameAndIsDeletedFalse("admin").isEmpty()) {
                var adminRole = roleRepository.findByCodeAndIsDeletedFalse("SUPER_ADMIN")
                        .orElseThrow(() -> new RuntimeException("Role SUPER_ADMIN not found"));

                Staff admin = Staff.builder()
                        .username("admin")
                        .password(passwordEncoder.encode(adminPassword))
                        .firstName("Hệ thống")
                        .lastName("Quản trị")
                        .fullName("Quản trị Hệ thống")
                        .role(adminRole)
                        .status(ActiveStatus.ACTIVE)
                        .isDeleted(false)
                        .build();
                staffRepository.save(admin);
                log.info("Admin user seeded successfully");
            }

            // Seed Manager cho khách sạn (Ví dụ 1 khách sạn)
            if (staffRepository.findByUsernameAndIsDeletedFalse("manager_hn").isEmpty()) {
                var hotel = hotelRepository.findByNameAndIsDeletedFalse("Khách sạn Viettel Hà Nội")
                        .orElse(null); // Hoặc throw exception nếu bắt buộc phải có hotel
                var managerRole = roleRepository.findByCodeAndIsDeletedFalse("HOTEL_MANAGER")
                        .orElseThrow();

                Staff manager = Staff.builder()
                        .username("manager_hn")
                        .password(passwordEncoder.encode(staffPassword))
                        .hotel(hotel)
                        .role(managerRole)
                        .firstName("Trần")
                        .lastName("Quản Lý")
                        .fullName("Trần Quản Lý (HN)")
                        .status(ActiveStatus.ACTIVE)
                        .isDeleted(false)
                        .build();
                staffRepository.save(manager);
                log.info("Manager user seeded successfully");
            }
            // Tiếp tục cho receptionist, housekeeping...
        };
    }
}