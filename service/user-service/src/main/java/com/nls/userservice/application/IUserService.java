package com.nls.userservice.application;

import com.nls.userservice.api.dto.request.LoginReq;
import com.nls.userservice.api.dto.response.UserRes;
import com.nls.userservice.shared.base.ApiResponse;

public interface IUserService {

    ApiResponse<UserRes> login(LoginReq loginReq);

}
