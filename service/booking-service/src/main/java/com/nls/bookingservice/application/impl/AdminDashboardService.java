package com.nls.bookingservice.application.impl;

import com.nls.bookingservice.api.dto.request.AdminDashboardReq;
import com.nls.bookingservice.api.dto.response.AdminDashboardRes;
import com.nls.bookingservice.api.dto.response.BookingStats;
import com.nls.bookingservice.api.dto.response.GrowthMetrics;
import com.nls.bookingservice.api.dto.response.RevenueData;
import com.nls.bookingservice.application.IAdminDashboardService;
import com.nls.bookingservice.domain.repository.BookingRepository;
import com.nls.bookingservice.domain.repository.PropertyRepository;
import com.nls.bookingservice.infrastructure.external.client.PaymentClient;
import com.nls.bookingservice.infrastructure.external.client.UserClient;
import com.nls.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardService implements IAdminDashboardService {

    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;
    private final UserClient userClient;
    private final PaymentClient paymentClient;

    @Override
    public ApiResponse<AdminDashboardRes> getDashboardStats(AdminDashboardReq request) {
        try {
            log.info("Calculating dashboard stats for period: {} to {}",
                    request.fromDate(), request.toDate());

            LocalDateTime fromDateTime = request.fromDate().atStartOfDay();
            LocalDateTime toDateTime = request.toDate().atTime(23, 59, 59);

            // 1. Property counts
            Long totalProperties = propertyRepository.countPropertiesInDateRange(fromDateTime, toDateTime);

            // 2. New users count (call user service)
            Long newUsers = getUserCount(fromDateTime, toDateTime);

            // 3. Host count
            Long totalHosts = propertyRepository.countDistinctHostsInDateRange(fromDateTime, toDateTime);

            // 4. Revenue data (call payment service)
            RevenueData revenueData = getRevenueData(fromDateTime, toDateTime);

            // 5. Booking statistics
            BookingStats bookingStats = getBookingStats(fromDateTime, toDateTime);

            // 6. Growth rates (optional - compare with previous period)
            GrowthMetrics growth = calculateGrowthMetrics(request, revenueData.totalRevenue());

            AdminDashboardRes response = AdminDashboardRes.builder()
                    .totalProperties(totalProperties)
                    .newUsers(newUsers)
                    .totalHosts(totalHosts)
                    .totalRevenue(revenueData.totalRevenue())
                    .completedPayments(revenueData.completedPayments())
                    .pendingPayments(revenueData.pendingPayments())
                    .fromDate(request.fromDate())
                    .toDate(request.toDate())
                    .totalBookings(bookingStats.totalBookings())
                    .completedBookings(bookingStats.completedBookings())
                    .cancelledBookings(bookingStats.cancelledBookings())
                    .userGrowthRate(growth.userGrowthRate())
                    .revenueGrowthRate(growth.revenueGrowthRate())
                    .build();

            log.info("Dashboard stats calculated successfully");
            return ApiResponse.ok(response);

        } catch (Exception e) {
            log.error("Error calculating dashboard stats: {}", e.getMessage(), e);
            return ApiResponse.internalError();
        }
    }

    private Long getUserCount(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        try {
            // Call user service to get new user count
            ApiResponse<Long> userCountResponse = userClient.getNewUserCount(fromDateTime, toDateTime);
            return userCountResponse.data() != null ? userCountResponse.data() : 0L;
        } catch (Exception e) {
            log.error("Error getting user count: {}", e.getMessage());
            return 0L;
        }
    }

    private RevenueData getRevenueData(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        try {
            // Call payment service to get revenue data
            ApiResponse<RevenueData> revenueResponse = paymentClient.getRevenueData(fromDateTime, toDateTime);
            return revenueResponse.data() != null ? revenueResponse.data() :
                    RevenueData.builder()
                            .totalRevenue(BigDecimal.ZERO)
                            .completedPayments(BigDecimal.ZERO)
                            .pendingPayments(BigDecimal.ZERO)
                            .build();
        } catch (Exception e) {
            log.error("Error getting revenue data: {}", e.getMessage());
            return RevenueData.builder()
                    .totalRevenue(BigDecimal.ZERO)
                    .completedPayments(BigDecimal.ZERO)
                    .pendingPayments(BigDecimal.ZERO)
                    .build();
        }
    }

    private BookingStats getBookingStats(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        try {
            Long totalBookings = bookingRepository.countBookingsInDateRange(fromDateTime, toDateTime);
            Long completedBookings = bookingRepository.countBookingsByStatusInDateRange(
                    "CONFIRMED", fromDateTime, toDateTime);
            Long cancelledBookings = bookingRepository.countBookingsByStatusInDateRange(
                    "CANCELLED", fromDateTime, toDateTime);

            return BookingStats.builder()
                    .totalBookings(totalBookings)
                    .completedBookings(completedBookings)
                    .cancelledBookings(cancelledBookings)
                    .build();
        } catch (Exception e) {
            log.error("Error getting booking stats: {}", e.getMessage());
            return BookingStats.builder()
                    .totalBookings(0L)
                    .completedBookings(0L)
                    .cancelledBookings(0L)
                    .build();
        }
    }

    private GrowthMetrics calculateGrowthMetrics(AdminDashboardReq request, BigDecimal currentRevenue) {
        try {
            // Calculate previous period (same duration before fromDate)
            long daysDiff = request.toDate().toEpochDay() - request.fromDate().toEpochDay();
            LocalDateTime prevFromDateTime = request.fromDate().minusDays(daysDiff + 1).atStartOfDay();
            LocalDateTime prevToDateTime = request.fromDate().minusDays(1).atTime(23, 59, 59);

            // Get previous period data
            Long prevUsers = getUserCount(prevFromDateTime, prevToDateTime);
            RevenueData prevRevenue = getRevenueData(prevFromDateTime, prevToDateTime);

            // Calculate growth rates
            Double userGrowthRate = calculateGrowthRate(getUserCount(
                    request.fromDate().atStartOfDay(), request.toDate().atTime(23, 59, 59)), prevUsers);
            Double revenueGrowthRate = calculateGrowthRate(currentRevenue, prevRevenue.totalRevenue());

            return GrowthMetrics.builder()
                    .userGrowthRate(userGrowthRate)
                    .revenueGrowthRate(revenueGrowthRate)
                    .build();
        } catch (Exception e) {
            log.error("Error calculating growth metrics: {}", e.getMessage());
            return GrowthMetrics.builder()
                    .userGrowthRate(0.0)
                    .revenueGrowthRate(0.0)
                    .build();
        }
    }

    private Double calculateGrowthRate(Number current, Number previous) {
        if (previous == null || previous.doubleValue() == 0) return 0.0;
        return ((current.doubleValue() - previous.doubleValue()) / previous.doubleValue()) * 100;
    }

    private Double calculateGrowthRate(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        return current.subtract(previous)
                .divide(previous, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"))
                .doubleValue();
    }
}