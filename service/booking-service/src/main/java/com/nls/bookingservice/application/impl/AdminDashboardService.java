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
import java.math.RoundingMode;
import java.time.LocalDate;
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

            // Validate date range
            if (request.fromDate().isAfter(request.toDate())) {
                log.warn("Invalid date range: fromDate {} is after toDate {}",
                        request.fromDate(), request.toDate());
                return ApiResponse.badRequest("From date cannot be after to date");
            }

            LocalDateTime fromDateTime = request.fromDate().atStartOfDay();
            LocalDateTime toDateTime = request.toDate().atTime(23, 59, 59);

            // 1. Property counts
            Long totalProperties = getTotalProperties(fromDateTime, toDateTime);

            // 2. New users count (call user service)
            Long newUsers = getUserCount(fromDateTime, toDateTime);

            // 3. Host count
            Long totalHosts = getTotalHosts(fromDateTime, toDateTime);

            // 4. Revenue data (call payment service) - FIXED: Use LocalDate
            RevenueData revenueData = getRevenueData(request.fromDate(), request.toDate());

            // 5. Booking statistics
            BookingStats bookingStats = getBookingStats(fromDateTime, toDateTime);

            // 6. Review count
            Long totalReviews = getReviewCount(fromDateTime, toDateTime);

            // 7. Growth rates (compare with previous period)
            GrowthMetrics growth = calculateGrowthMetrics(request, revenueData.totalRevenue(), totalReviews);

            AdminDashboardRes response = AdminDashboardRes.builder()
                    .totalProperties(totalProperties)
                    .newUsers(newUsers)
                    .totalHosts(totalHosts)
                    .totalReviews(totalReviews)
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

            log.info("Dashboard stats calculated successfully - Properties: {}, Users: {}, Reviews: {}, Revenue: {}",
                    totalProperties, newUsers, totalReviews, revenueData.totalRevenue());
            return ApiResponse.ok(response);

        } catch (Exception e) {
            log.error("Error calculating dashboard stats: {}", e.getMessage(), e);
            return ApiResponse.internalError();
        }
    }

    private Long getTotalProperties(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        try {
            return propertyRepository.countPropertiesInDateRange(fromDateTime, toDateTime);
        } catch (Exception e) {
            log.error("Error getting property count: {}", e.getMessage());
            return 0L;
        }
    }

    private Long getTotalHosts(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        try {
            return propertyRepository.countDistinctHostsInDateRange(fromDateTime, toDateTime);
        } catch (Exception e) {
            log.error("Error getting host count: {}", e.getMessage());
            return 0L;
        }
    }

    private Long getUserCount(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        try {
            ApiResponse<Long> userCountResponse = userClient.getNewUserCount(fromDateTime, toDateTime);
            if (userCountResponse != null && userCountResponse.data() != null) {
                return userCountResponse.data();
            }
            log.warn("User service returned null response");
            return 0L;
        } catch (Exception e) {
            log.error("Error getting user count: {}", e.getMessage());
            return 0L;
        }
    }

    private Long getReviewCount(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        try {
            ApiResponse<Long> reviewCountResponse = userClient.getReviewCount(fromDateTime, toDateTime);
            if (reviewCountResponse != null && reviewCountResponse.data() != null) {
                return reviewCountResponse.data();
            }
            log.warn("User service returned null review count");
            return 0L;
        } catch (Exception e) {
            log.error("Error getting review count: {}", e.getMessage());
            return 0L;
        }
    }

    // FIXED: Use LocalDate instead of LocalDateTime for Payment Service
    private RevenueData getRevenueData(LocalDate fromDate, LocalDate toDate) {
        try {
            log.debug("Calling payment service for revenue data from {} to {}", fromDate, toDate);
            ApiResponse<RevenueData> revenueResponse = paymentClient.getRevenueData(fromDate, toDate);

            if (revenueResponse != null && revenueResponse.data() != null) {
                log.debug("Revenue data received: {}", revenueResponse.data());
                return revenueResponse.data();
            }

            log.warn("Payment service returned null revenue data");
            return createEmptyRevenueData();
        } catch (Exception e) {
            log.error("Error getting revenue data from payment service: {}", e.getMessage());
            return createEmptyRevenueData();
        }
    }

    private RevenueData createEmptyRevenueData() {
        return RevenueData.builder()
                .totalRevenue(BigDecimal.ZERO)
                .completedPayments(0L)
                .pendingPayments(0L)
                .build();
    }

    private BookingStats getBookingStats(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        try {
            Long totalBookings = bookingRepository.countBookingsInDateRange(fromDateTime, toDateTime);
            Long completedBookings = bookingRepository.countBookingsByStatusInDateRange(
                    "PAID", fromDateTime, toDateTime);
            Long cancelledBookings = bookingRepository.countBookingsByStatusInDateRange(
                    "PENDING", fromDateTime, toDateTime);

            return BookingStats.builder()
                    .totalBookings(totalBookings != null ? totalBookings : 0L)
                    .completedBookings(completedBookings != null ? completedBookings : 0L)
                    .cancelledBookings(cancelledBookings != null ? cancelledBookings : 0L)
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

    // FIXED: Updated to use LocalDate for payment service calls
    private GrowthMetrics calculateGrowthMetrics(AdminDashboardReq request, BigDecimal currentRevenue, Long currentReviews) {
        try {
            // Calculate previous period (same duration before fromDate)
            long daysDiff = request.toDate().toEpochDay() - request.fromDate().toEpochDay();
            LocalDate prevFromDate = request.fromDate().minusDays(daysDiff + 1);
            LocalDate prevToDate = request.fromDate().minusDays(1);

            LocalDateTime prevFromDateTime = prevFromDate.atStartOfDay();
            LocalDateTime prevToDateTime = prevToDate.atTime(23, 59, 59);

            log.debug("Calculating growth metrics - Current period: {} to {}, Previous period: {} to {}",
                    request.fromDate(), request.toDate(), prevFromDate, prevToDate);

            // Get previous period data
            Long prevUsers = getUserCount(prevFromDateTime, prevToDateTime);
            RevenueData prevRevenue = getRevenueData(prevFromDate, prevToDate); // FIXED: Use LocalDate
            Long prevReviews = getReviewCount(prevFromDateTime, prevToDateTime);

            // Get current period user count for growth calculation
            Long currentUsers = getUserCount(request.fromDate().atStartOfDay(), request.toDate().atTime(23, 59, 59));

            // Calculate growth rates
            Double userGrowthRate = calculateGrowthRate(currentUsers, prevUsers);
            Double revenueGrowthRate = calculateGrowthRate(currentRevenue, prevRevenue.totalRevenue());
            Double reviewGrowthRate = calculateGrowthRate(currentReviews, prevReviews);

            log.debug("Growth rates calculated - Users: {}%, Revenue: {}%, Reviews: {}%",
                    userGrowthRate, revenueGrowthRate, reviewGrowthRate);

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
        if (previous == null || current == null) {
            log.debug("Null values in growth calculation - current: {}, previous: {}", current, previous);
            return 0.0;
        }

        double prevValue = previous.doubleValue();
        double currValue = current.doubleValue();

        if (prevValue == 0) {
            return currValue > 0 ? 100.0 : 0.0; // 100% growth if previous was 0 and current > 0
        }

        return ((currValue - prevValue) / prevValue) * 100;
    }

    private Double calculateGrowthRate(BigDecimal current, BigDecimal previous) {
        if (previous == null || current == null) {
            log.debug("Null BigDecimal values in growth calculation - current: {}, previous: {}", current, previous);
            return 0.0;
        }

        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }

        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .doubleValue();
    }
}