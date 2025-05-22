package com.nls.bookingservice.api.controller;

import com.nls.bookingservice.api.dto.request.CreatePropertyReq;
import com.nls.bookingservice.api.dto.response.PropertyRes;
import com.nls.bookingservice.application.IPropertyService;
import com.nls.bookingservice.shared.base.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("property")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PropertyController {

    IPropertyService propertyService;

    @GetMapping("/{propertyId}")
    public ResponseEntity<ApiResponse<PropertyRes>> getPropertyById(@PathVariable UUID propertyId) {
        return ResponseEntity.ok(propertyService.getPropertyById(propertyId));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<PropertyRes>> addProperty(@RequestBody CreatePropertyReq request) {
        return ResponseEntity.ok(propertyService.addProperty(request));
    }
}