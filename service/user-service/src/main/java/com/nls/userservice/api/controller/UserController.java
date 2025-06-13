package com.nls.userservice.api.controller;

import com.nls.common.dto.response.ApiResponse;
import com.nls.userservice.api.dto.request.ChangeRoleReq;
import com.nls.userservice.api.dto.request.ForgetPasswordReq;
import com.nls.userservice.api.dto.request.ResetPasswordReq;
import com.nls.userservice.api.dto.request.UpdateUserReq;
import com.nls.common.dto.response.UserRes;
import com.nls.userservice.application.IUserService;
import jakarta.validation.Valid;
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

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserRes>> getUserProfile(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserProfileByUserId(userId));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Void>> updateUserProfile(@RequestBody UpdateUserReq request) {
        return ResponseEntity.ok(userService.updateUser(request));
    }

    @GetMapping("/confirm")
    public ResponseEntity<ApiResponse<UserRes>> confirmToken(@RequestParam String token) {
        return ResponseEntity.ok(userService.confirmToken(token));
    }

    @PostMapping("/forget-password")
    public ResponseEntity<ApiResponse<Void>> forgetPassword(@RequestBody ForgetPasswordReq request) {
        return ResponseEntity.ok(userService.forgetPassword(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<UserRes>> resetPassword(@RequestBody ResetPasswordReq request) {
        return ResponseEntity.ok(userService.resetPassword(request));
    }
    @PostMapping("/change-role")
    public ResponseEntity<ApiResponse<UserRes>> changeUserRole(@RequestBody @Valid ChangeRoleReq request) {
        return ResponseEntity.ok(userService.changeUserRole(request));
    }
}
