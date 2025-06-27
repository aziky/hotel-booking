package com.nls.bookingservice.api.controller;

import com.nls.bookingservice.api.dto.request.AdminDashboardReq;
import com.nls.bookingservice.api.dto.response.AdminDashboardRes;
import com.nls.bookingservice.application.IAdminDashboardService;
import com.nls.common.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Dashboard", description = "Admin dashboard statistics")
@SecurityRequirement(name = "bearerAuth")
public class AdminDashboardController {

    private final IAdminDashboardService adminDashboardService;

    @GetMapping
    @Operation(summary = "Get admin dashboard statistics")
    public ApiResponse<AdminDashboardRes> getDashboardStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        log.info("Getting admin dashboard stats from {} to {}", fromDate, toDate);

        AdminDashboardReq request = AdminDashboardReq.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        return adminDashboardService.getDashboardStats(request);
    }

    @PostMapping
    @Operation(summary = "Get admin dashboard statistics (POST version for complex filters)")
    public ApiResponse<AdminDashboardRes> getDashboardStatsPost(
            @Valid @RequestBody AdminDashboardReq request) {

        log.info("Getting admin dashboard stats from {} to {}",
                request.fromDate(), request.toDate());

        return adminDashboardService.getDashboardStats(request);
    }
}
