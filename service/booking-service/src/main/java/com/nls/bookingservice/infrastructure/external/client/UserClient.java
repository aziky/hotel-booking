package com.nls.bookingservice.infrastructure.external.client;

import com.nls.bookingservice.shared.base.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient("user-service")
public interface UserClient {

    @GetMapping("/review/property/{propertyId}/rating")
    ApiResponse<Double> getAverageRatingByPropertyId(@PathVariable UUID propertyId);
}