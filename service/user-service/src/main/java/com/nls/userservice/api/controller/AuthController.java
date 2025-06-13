package com.nls.userservice.api.controller;


import com.nls.common.dto.response.ApiResponse;
import com.nls.userservice.api.dto.request.CreateUserReq;
import com.nls.userservice.api.dto.request.LoginReq;
import com.nls.common.dto.response.UserRes;
import com.nls.userservice.application.IUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    IUserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserRes>> login(@RequestBody LoginReq request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> registerUserAccount(@RequestBody CreateUserReq request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

}
