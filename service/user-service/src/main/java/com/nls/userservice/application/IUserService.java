package com.nls.userservice.application;

import com.nls.common.dto.response.ApiResponse;
import com.nls.userservice.api.dto.request.*;
import com.nls.userservice.api.dto.response.UserRes;

public interface IUserService {

    ApiResponse<UserRes> login(LoginReq loginReq);

    ApiResponse<UserRes> getUserProfileByUserId();

    ApiResponse<Void> createUser(CreateUserReq request);

    ApiResponse<Void> updateUser(UpdateUserReq request);

    ApiResponse<UserRes> confirmToken(String token);

    ApiResponse<Void> forgetPassword(ForgetPasswordReq request);

    ApiResponse<UserRes> resetPassword(ResetPasswordReq request);

}
