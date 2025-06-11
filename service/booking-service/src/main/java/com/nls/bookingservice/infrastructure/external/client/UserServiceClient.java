package com.nls.bookingservice.infrastructure.external.client;

import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.UserRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient("user-service")
public interface UserServiceClient {

    @GetMapping("/review/property/{propertyId}/rating")
    ApiResponse<Double> getAverageRatingByPropertyId(@PathVariable UUID propertyId);

    @GetMapping("/{userId}")
    ApiResponse<UserRes> getUserById(@PathVariable UUID userId);
}