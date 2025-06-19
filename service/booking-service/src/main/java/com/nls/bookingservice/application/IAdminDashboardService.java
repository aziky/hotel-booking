package com.nls.bookingservice.application;

import com.nls.bookingservice.api.dto.request.AdminDashboardReq;
import com.nls.bookingservice.api.dto.response.AdminDashboardRes;
import com.nls.common.dto.response.ApiResponse;

public interface IAdminDashboardService {
    ApiResponse<AdminDashboardRes> getDashboardStats(AdminDashboardReq request);
}