package com.nls.bookingservice.application;

import com.nls.bookingservice.api.dto.request.CreatePropertyReq;
import com.nls.bookingservice.api.dto.request.UpdatePropertyReq;
import com.nls.bookingservice.api.dto.response.PagedPropertyRes;
import com.nls.bookingservice.api.dto.response.PropertyDetailRes;
import com.nls.bookingservice.api.dto.response.PropertyRes;
import com.nls.common.dto.response.ApiResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IPropertyService {

    ApiResponse<PropertyDetailRes> getPropertyDetail(UUID propertyId);

    ApiResponse<PropertyRes> addProperty(CreatePropertyReq request);

    ApiResponse<PagedPropertyRes> getProperties(Pageable pageable);

    ApiResponse<PropertyRes> updateProperty(UpdatePropertyReq request);

    ApiResponse<PropertyRes> deleteProperty(UUID propertyId);
}