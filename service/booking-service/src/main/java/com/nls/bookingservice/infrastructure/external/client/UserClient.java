package com.nls.bookingservice.infrastructure.external.client;

import com.nls.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.UUID;

@FeignClient("user-service")
public interface UserClient {

    String BASE_USER = "/api/user";

    @GetMapping(BASE_USER + "/review/property/{propertyId}/rating")
    ApiResponse<Double> getAverageRatingByPropertyId(@PathVariable UUID propertyId);

    @GetMapping("/api/admin/users/count")
    ApiResponse<Long> getNewUserCount(@RequestParam LocalDateTime fromDate,
                                      @RequestParam LocalDateTime toDate);
}