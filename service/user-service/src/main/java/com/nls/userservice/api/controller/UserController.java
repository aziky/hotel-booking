package com.nls.userservice.api.controller;

import com.nls.common.dto.response.ApiResponse;
import com.nls.userservice.api.dto.request.UpdateUserReq;
import com.nls.userservice.api.dto.response.UserRes;
import com.nls.userservice.application.IUserService;
import com.nls.userservice.shared.utils.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    IUserService userService;

    @GetMapping()
    public ResponseEntity<ApiResponse<UserRes>> getUserProfile() {
        UUID userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(userService.getUserProfileByUserId(userId));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Void>> updateUserProfile(@RequestBody UpdateUserReq request) {
        UUID userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }
}
