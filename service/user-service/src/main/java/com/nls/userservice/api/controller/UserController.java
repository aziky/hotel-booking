package com.nls.userservice.api.controller;

import com.nls.common.dto.response.ApiResponse;
import com.nls.userservice.api.dto.request.ChangeRoleReq;
import com.nls.userservice.api.dto.request.ForgetPasswordReq;
import com.nls.userservice.api.dto.request.ResetPasswordReq;
import com.nls.userservice.api.dto.request.UpdateUserReq;
import com.nls.common.dto.response.UserRes;
import com.nls.userservice.application.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<ApiResponse<UserRes>> changeUserRole() {
        return ResponseEntity.ok(userService.changeUserRole());
    }
    @GetMapping("/count/users")
    @Operation(summary = "Count users with USER role", description = "Get the number of users with USER role")
    public ResponseEntity<ApiResponse<Long>> countUsers() {
        ApiResponse<Long> response = userService.countUsersByRole("USER");
        return ResponseEntity.ok(response);
    }
    @GetMapping("/user-list")
    public ResponseEntity<ApiResponse<List<UserRes>>> getUsers() {
        return ResponseEntity.ok(userService.getUsersByRole("USER"));
    }
}
