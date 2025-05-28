package com.nls.userservice.application;

import com.nls.common.dto.response.ApiResponse;
import com.nls.userservice.api.dto.request.CreateUserReq;
import com.nls.userservice.api.dto.request.LoginReq;
import com.nls.userservice.api.dto.request.UpdateUserReq;
import com.nls.userservice.api.dto.response.UserRes;

public interface IUserService {

    ApiResponse<UserRes> login(LoginReq loginReq);

    ApiResponse<UserRes> getUserProfileByUserId();

    ApiResponse<Void> createUser(CreateUserReq request);

    ApiResponse<Void> updateUser(UpdateUserReq request);

    ApiResponse<UserRes> confirmToken(String token);

}
