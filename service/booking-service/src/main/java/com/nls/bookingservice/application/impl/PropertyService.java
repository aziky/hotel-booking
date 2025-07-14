package com.nls.bookingservice.application.impl;

import com.nls.bookingservice.api.dto.request.CreatePropertyReq;
import com.nls.bookingservice.api.dto.request.UpdatePropertyReq;
import com.nls.bookingservice.api.dto.response.PagedPropertyRes;
import com.nls.bookingservice.api.dto.response.PropertyDetailRes;
import com.nls.bookingservice.api.dto.response.PropertyRes;
import com.nls.bookingservice.application.IPropertyService;
import com.nls.bookingservice.domain.entity.Property;
import com.nls.bookingservice.domain.entity.PropertyDayPrice;
import com.nls.bookingservice.domain.entity.PropertyImage;
import com.nls.bookingservice.domain.entity.PropertyStatus;
import com.nls.bookingservice.domain.repository.PropertyDayPriceRepository;
import com.nls.bookingservice.domain.repository.PropertyImageRepository;
import com.nls.bookingservice.domain.repository.PropertyRepository;
import com.nls.bookingservice.shared.mapper.PropertyMapper;
import com.nls.bookingservice.shared.utils.SecurityUtil; // Add this import
import com.nls.common.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PropertyService implements IPropertyService {

    PropertyRepository propertyRepository;
    PropertyImageRepository propertyImageRepository;
    PropertyDayPriceRepository propertyDayPriceRepository;
    PropertyMapper propertyMapper;

    @Override
    public ApiResponse<PropertyDetailRes> getPropertyDetail(UUID propertyId) {
        try {
            log.info("Start handle at get property detail with id {}", propertyId);
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new RuntimeException("Property not found with id " + propertyId));

            // Ensure day prices are loaded
            if (property.getDayPrices() != null) {
                property.getDayPrices().size(); // Trigger lazy loading
            }

            PropertyDetailRes detailRes = propertyMapper.convertToPropertyDetailRes(property);
            log.info("Get property detail successfully with current day price: {}", detailRes.currentDayPrice());
            return ApiResponse.ok(detailRes);
        } catch (RuntimeException e) {
            log.warn("Get property detail failed cause by {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error at get property detail by id cause by {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }

    @Override
    @Transactional
    public ApiResponse<PropertyRes> addProperty(CreatePropertyReq request) {
        try {
            // Get current user as host
            UUID currentUserId = SecurityUtil.getCurrentUserId();
            log.info("Start add property with the request {} for host {}", request, currentUserId);

            Property property = propertyMapper.convertCreatePropertyReqToProperty(request);
            // Set the current user as the host
            property.setHostId(currentUserId);
            property = propertyRepository.save(property);

            // Save day prices
            if (request.dayPrices() != null && !request.dayPrices().isEmpty()) {
                List<PropertyDayPrice> dayPrices = new ArrayList<>();
                for (CreatePropertyReq.DayPriceReq dayPriceReq : request.dayPrices()) {
                    PropertyDayPrice dayPrice = new PropertyDayPrice();
                    dayPrice.setPropertyId(property.getId());
                    dayPrice.setDayOfWeek(dayPriceReq.dayOfWeek());
                    dayPrice.setPrice(dayPriceReq.price());
                    dayPrices.add(dayPrice);
                }
                propertyDayPriceRepository.saveAll(dayPrices);
                property.setDayPrices(dayPrices);
                log.info("Added {} day prices for property", dayPrices.size());
            }

            // Save image if provided
            if (request.imageUrl() != null && !request.imageUrl().isEmpty()) {
                PropertyImage propertyImage = propertyMapper.createPropertyImageFromRequest(request, property.getId());
                propertyImageRepository.save(propertyImage);
                log.info("Added property image with URL: {}", request.imageUrl());
            }

            log.info("Add property successfully with id {} for host {}", property.getId(), currentUserId);
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

            Page<Property> propertyPage = propertyRepository.findByStatus(PropertyStatus.ACTIVE, pageable);;

            // Trigger lazy loading of day prices for current day price calculation
            propertyPage.getContent().forEach(property -> {
                if (property.getDayPrices() != null) {
                    property.getDayPrices().size(); // Trigger lazy loading
                }
            });
            PagedPropertyRes pagedPropertyRes = propertyMapper.convertToPagedPropertyRes(propertyPage);

            log.info("Get properties successfully with total elements {}", propertyPage.getTotalElements());
            return ApiResponse.ok(pagedPropertyRes);
        } catch (Exception e) {
            log.error("Error at get properties cause by {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }

    @Override
    @Transactional
    public ApiResponse<PropertyRes> updateProperty(UpdatePropertyReq request) {
        try {
            UUID currentUserId = SecurityUtil.getCurrentUserId();
            log.info("Start update property with id {} by user {}", request.id(), currentUserId);

            Property property = propertyRepository.findById(request.id())
                    .orElseThrow(() -> new RuntimeException("Property not found with id " + request.id()));


            // Handle day prices update using simple UPSERT approach
            if (request.dayPrices() != null) {
                upsertDayPrices(property.getId(), request.dayPrices(), currentUserId);
                log.info("Upserted {} day prices for property", request.dayPrices().size());
            }

            // Save the property
            property = propertyRepository.save(property);

            log.info("Update property successfully with id {} by user {}", property.getId(), currentUserId);
            return ApiResponse.ok(propertyMapper.convertToPropertyRes(property));

        } catch (RuntimeException e) {
            log.warn("Update property failed cause by {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error at update property: {}", e.getMessage(), e);
            return ApiResponse.internalError();
        }
    }
    private void upsertDayPrices(UUID propertyId, List<UpdatePropertyReq.DayPriceReq> requestDayPrices, UUID currentUserId) {
        for (UpdatePropertyReq.DayPriceReq requestDayPrice : requestDayPrices) {
            // Try to find existing day price
            Optional<PropertyDayPrice> existingOpt = propertyDayPriceRepository
                    .findByPropertyIdAndDayOfWeek(propertyId, requestDayPrice.dayOfWeek());

            if (existingOpt.isPresent()) {
                // Update existing
                PropertyDayPrice existing = existingOpt.get();
                existing.setPrice(requestDayPrice.price());
                propertyDayPriceRepository.save(existing);
            } else {
                // Create new
                PropertyDayPrice newDayPrice = PropertyDayPrice.builder()
                        .propertyId(propertyId)
                        .dayOfWeek(requestDayPrice.dayOfWeek())
                        .price(requestDayPrice.price())
                        .build();
                propertyDayPriceRepository.save(newDayPrice);
            }
        }
    }
    @Override
    @Transactional
    public ApiResponse<PropertyRes> deleteProperty(UUID propertyId) {
        try {
            UUID currentUserId = SecurityUtil.getCurrentUserId();
            log.info("Start delete property with id {} by user {}", propertyId, currentUserId);

            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new RuntimeException("Property not found with id " + propertyId));

            // Change status to INACTIVE instead of physically deleting
            property.setStatus(PropertyStatus.INACTIVE);
            property = propertyRepository.save(property);

            log.info("Delete property (set to INACTIVE) successfully with id {} by user {}",
                    property.getId(), currentUserId);
            return ApiResponse.ok(propertyMapper.convertToPropertyRes(property));
        } catch (RuntimeException e) {
            log.warn("Delete property failed cause by {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error at delete property cause by {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }
    @Override
    public ApiResponse<PagedPropertyRes> getMyProperties(Pageable pageable) {
        try {
            UUID currentUserId = SecurityUtil.getCurrentUserId();
            log.info("Getting properties for host: {} with page {} and size {}",
                    currentUserId, pageable.getPageNumber(), pageable.getPageSize());

            Page<Property> propertyPage = propertyRepository.findByHostId(currentUserId, pageable);

            // Trigger lazy loading of day prices
            propertyPage.getContent().forEach(property -> {
                if (property.getDayPrices() != null) {
                    property.getDayPrices().size();
                }
            });

            PagedPropertyRes pagedPropertyRes = propertyMapper.convertToPagedPropertyRes(propertyPage);

            log.info("Found {} properties for host: {}", propertyPage.getTotalElements(), currentUserId);
            return ApiResponse.ok(pagedPropertyRes);

        } catch (Exception e) {
            log.error("Error getting properties for current user: {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }
}