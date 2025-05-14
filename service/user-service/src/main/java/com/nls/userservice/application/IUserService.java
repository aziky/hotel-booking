package com.nls.userservice.application;

import com.nls.userservice.api.dto.request.LoginReq;
import com.nls.userservice.api.dto.response.UserRes;
import com.nls.userservice.shared.base.ApiResponse;

import java.util.UUID;

public interface IUserService {

    ApiResponse<UserRes> login(LoginReq loginReq);

    ApiResponse<UserRes> getUserProfileByUserId(UUID userId);

}
