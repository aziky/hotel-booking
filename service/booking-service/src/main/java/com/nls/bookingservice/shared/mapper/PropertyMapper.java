package com.nls.bookingservice.shared.mapper;

import com.nls.bookingservice.api.dto.request.CreatePropertyReq;
import com.nls.bookingservice.api.dto.response.*;
import com.nls.bookingservice.domain.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

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
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    Property convertCreatePropertyReqToProperty(CreatePropertyReq request);
}
