package com.nls.bookingservice.shared.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nls.bookingservice.api.dto.request.CreatePropertyReq;
import com.nls.bookingservice.api.dto.request.UpdatePropertyReq;
import com.nls.bookingservice.api.dto.response.*;
import com.nls.bookingservice.domain.entity.*;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PropertyMapper {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Named("mapToJson")
    default String mapToJson(Map<String, String> map) {
        try {
            return OBJECT_MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting map to JSON", e);
        }
    }

    PropertyRes convertToPropertyRes(Property property);

    @Mapping(target = "currentDayPrice", expression = "java(getCurrentDayPrice(property))")
    PropertyDetailRes convertToPropertyDetailRes(Property property);

    default BigDecimal getCurrentDayPrice(Property property) {
        if (property.getDayPrices() == null || property.getDayPrices().isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Get current day of week (1=Monday, 7=Sunday)
        int currentDayOfWeek = LocalDate.now().getDayOfWeek().getValue();

        return property.getDayPrices().stream()
                .filter(dp -> dp.getDayOfWeek() == currentDayOfWeek)
                .map(PropertyDayPrice::getPrice)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    PropertyImageRes convertToPropertyImageRes(PropertyImage propertyImage);

    PropertyAmenityRes convertToPropertyAmenityRes(PropertyAmenity propertyAmenity);

    PropertyCategoryRes convertToPropertyCategoryRes(PropertyCategory propertyCategory);

    PropertyDayPriceRes convertToPropertyDayPriceRes(PropertyDayPrice propertyDayPrice);

    List<PropertyDetailRes> convertToPropertyDetailResList(List<Property> properties);

    default PagedPropertyRes convertToPagedPropertyRes(Page<Property> propertyPage) {
        return PagedPropertyRes.builder()
                .properties(convertToPropertyDetailResList(propertyPage.getContent()))
                .page(propertyPage.getNumber())
                .size(propertyPage.getSize())
                .totalElements(propertyPage.getTotalElements())
                .totalPages(propertyPage.getTotalPages())
                .build();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "dayPrices", ignore = true) // Will handle manually in afterMapping
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "status", expression = "java(com.nls.bookingservice.domain.entity.PropertyStatus.ACTIVE)")
    Property convertCreatePropertyReqToProperty(CreatePropertyReq request);

    @AfterMapping
    default void mapDayPricesFromCreateReq(CreatePropertyReq request, @MappingTarget Property property) {
        if (request.dayPrices() != null && !request.dayPrices().isEmpty()) {
            List<PropertyDayPrice> dayPrices = new ArrayList<>();
            for (CreatePropertyReq.DayPriceReq dayPriceReq : request.dayPrices()) {
                PropertyDayPrice dayPrice = new PropertyDayPrice();
                dayPrice.setDayOfWeek(dayPriceReq.dayOfWeek());
                dayPrice.setPrice(dayPriceReq.price());
                dayPrices.add(dayPrice);
            }
            property.setDayPrices(dayPrices);
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hostId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "dayPrices", ignore = true) // Will handle manually in afterMapping
    @Mapping(target = "bookings", ignore = true)
    void updatePropertyFromReq(UpdatePropertyReq request, @MappingTarget Property property);

    @AfterMapping
    default void mapDayPricesFromUpdateReq(UpdatePropertyReq request, @MappingTarget Property property) {
        if (request.dayPrices() != null) {
            // Clear existing day prices
            if (property.getDayPrices() != null) {
                property.getDayPrices().clear();
            } else {
                property.setDayPrices(new ArrayList<>());
            }

            // Add new day prices
            for (UpdatePropertyReq.DayPriceReq dayPriceReq : request.dayPrices()) {
                PropertyDayPrice dayPrice = new PropertyDayPrice();
                dayPrice.setPropertyId(property.getId());
                dayPrice.setDayOfWeek(dayPriceReq.dayOfWeek());
                dayPrice.setPrice(dayPriceReq.price());
                dayPrice.setUpdatedBy(request.updatedBy());
                property.getDayPrices().add(dayPrice);
            }
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "propertyId", source = "propertyId")
    @Mapping(target = "imageUrl", source = "request.imageUrl")
    @Mapping(target = "caption", ignore = true)
    @Mapping(target = "isPrimary", constant = "true")
    @Mapping(target = "displayOrder", constant = "0")
    @Mapping(target = "property", ignore = true)
    PropertyImage createPropertyImageFromRequest(CreatePropertyReq request, UUID propertyId);
}