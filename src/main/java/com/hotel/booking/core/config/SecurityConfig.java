package com.hotel.booking.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // THAY ĐỔI TẠI ĐÂY: Kích hoạt CORS và ăn theo cấu hình CorsConfigurationSource
                // bên dưới
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    // CẤU HÌNH CỦA BỘ LỌC CORS TOÀN CỤC
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ĐÃ CẬP NHẬT: Cho phép cả cổng Dev ngoài và cổng Nginx tiêu chuẩn của Docker
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost",
                "http://localhost:80"));

        // Cho phép đầy đủ các phương thức CRUD và request preflight OPTIONS
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Cho phép tất cả các loại Header gửi lên
        configuration.setAllowedHeaders(List.of("*"));

        // Cho phép gửi kèm Cookie hoặc thông tin xác thực nếu cần
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}