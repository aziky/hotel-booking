package com.nls.bookingservice.shared.mapper;

import com.nls.bookingservice.api.dto.request.CreatePropertyReq;
import com.nls.bookingservice.api.dto.request.UpdatePropertyReq;
import com.nls.bookingservice.api.dto.response.*;
import com.nls.bookingservice.domain.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PropertyMapper {

    PropertyRes convertToPropertyRes(Property property);

    PropertyDetailRes convertToPropertyDetailRes(Property property);

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
    @Mapping(target = "dayPrices", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "status", expression = "java(com.nls.bookingservice.domain.entity.PropertyStatus.ACTIVE)")
    Property convertCreatePropertyReqToProperty(CreatePropertyReq request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hostId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "dayPrices", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    void updatePropertyFromReq(UpdatePropertyReq request, @MappingTarget Property property);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "propertyId", source = "propertyId")
    @Mapping(target = "imageUrl", source = "request.imageUrl")
    @Mapping(target = "caption", ignore = true)
    @Mapping(target = "isPrimary", constant = "true")
    @Mapping(target = "displayOrder", constant = "0")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", source = "request.createdBy")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", source = "request.updatedBy")
    @Mapping(target = "property", ignore = true)
    PropertyImage createPropertyImageFromRequest(CreatePropertyReq request, UUID propertyId);
}
