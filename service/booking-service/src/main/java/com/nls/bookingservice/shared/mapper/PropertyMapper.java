package com.nls.bookingservice.shared.mapper;

import com.nls.bookingservice.api.dto.request.CreatePropertyReq;
import com.nls.bookingservice.api.dto.response.PropertyRes;
import com.nls.bookingservice.domain.entity.Property;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PropertyMapper {

    PropertyRes convertToPropertyRes(Property property);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "dayPrices", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    Property convertCreatePropertyReqToProperty(CreatePropertyReq request);
}