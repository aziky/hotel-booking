package com.nls.bookingservice.application;

import com.nls.bookingservice.api.dto.request.CreatePropertyReq;
import com.nls.bookingservice.api.dto.response.PagedPropertyRes;
import com.nls.bookingservice.api.dto.response.PropertyRes;
import com.nls.bookingservice.shared.base.ApiResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IPropertyService {

    ApiResponse<PropertyRes> getPropertyById(UUID propertyId);

    ApiResponse<PropertyRes> addProperty(CreatePropertyReq request);

    ApiResponse<PagedPropertyRes> getProperties(Pageable pageable);

}
