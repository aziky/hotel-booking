package com.nls.bookingservice.application.impl;

import com.nls.bookingservice.api.dto.request.CreatePropertyReq;
import com.nls.bookingservice.api.dto.response.PagedPropertyRes;
import com.nls.bookingservice.api.dto.response.PropertyRes;
import com.nls.bookingservice.application.IPropertyService;
import com.nls.bookingservice.domain.entity.Property;
import com.nls.bookingservice.domain.repository.PropertyRepository;
import com.nls.bookingservice.shared.base.ApiResponse;
import com.nls.bookingservice.shared.mapper.PropertyMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PropertyService implements IPropertyService {

    PropertyRepository propertyRepository;
    PropertyMapper propertyMapper;

    @Override
    public ApiResponse<PropertyRes> getPropertyById(UUID propertyId) {
        try {
            log.info("Start handle at get property with id {}", propertyId);
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new RuntimeException("Property not found with id " + propertyId));
            return ApiResponse.ok(propertyMapper.convertToPropertyRes(property));
        } catch (RuntimeException e) {
            log.warn("Get property failed cause by {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error at get property by id cause by {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }

    @Override
    public ApiResponse<PropertyRes> addProperty(CreatePropertyReq request) {
        try {
            log.info("Start add property with the request {}", request);

            Property property = propertyMapper.convertCreatePropertyReqToProperty(request);
            property = propertyRepository.save(property);

            log.info("Add property successfully with id {}", property.getId());
            return ApiResponse.created(propertyMapper.convertToPropertyRes(property));
        } catch (Exception e) {
            log.error("Error at add property cause by {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }

    @Override
    public ApiResponse<PagedPropertyRes> getProperties(Pageable pageable) {
        try {
            log.info("Start handle at get properties with page {} and size {}", 
                    pageable.getPageNumber(), pageable.getPageSize());

            Page<Property> propertyPage = propertyRepository.findAll(pageable);
            PagedPropertyRes pagedPropertyRes = propertyMapper.convertToPagedPropertyRes(propertyPage);

            log.info("Get properties successfully with total elements {}", propertyPage.getTotalElements());
            return ApiResponse.ok(pagedPropertyRes);
        } catch (Exception e) {
            log.error("Error at get properties cause by {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }
}
