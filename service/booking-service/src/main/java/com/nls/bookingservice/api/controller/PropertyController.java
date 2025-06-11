package com.nls.bookingservice.api.controller;

import com.nls.bookingservice.api.dto.request.CreatePropertyReq;
import com.nls.bookingservice.api.dto.request.UpdatePropertyReq;
import com.nls.bookingservice.api.dto.response.PagedPropertyRes;
import com.nls.bookingservice.api.dto.response.PropertyDetailRes;
import com.nls.bookingservice.api.dto.response.PropertyRes;
import com.nls.bookingservice.application.IPropertyService;
import com.nls.common.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<ApiResponse<PropertyDetailRes>> getPropertyById(@PathVariable UUID propertyId) {
        return ResponseEntity.ok(propertyService.getPropertyDetail(propertyId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedPropertyRes>> getProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(propertyService.getProperties(pageable));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<PropertyRes>> addProperty(@RequestBody CreatePropertyReq request) {
        return ResponseEntity.ok(propertyService.addProperty(request));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<PropertyRes>> updateProperty(@RequestBody UpdatePropertyReq request) {
        return ResponseEntity.ok(propertyService.updateProperty(request));
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<ApiResponse<PropertyRes>> deleteProperty(@PathVariable UUID propertyId) {
        return ResponseEntity.ok(propertyService.deleteProperty(propertyId));
    }
}