package com.nls.userservice.api.controller;

import com.nls.userservice.api.dto.response.UserRes;
import com.nls.userservice.application.IUserService;
import com.nls.userservice.shared.base.ApiResponse;
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
    public ResponseEntity<ApiResponse<UserRes>> getUserProfile(@RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(userService.getUserProfileByUserId(userId));
    }
}
