package com.nls.userservice.api.controller;

import com.nls.common.dto.response.ApiResponse;
import com.nls.userservice.api.dto.request.UpdateUserReq;
import com.nls.userservice.api.dto.response.UserRes;
import com.nls.userservice.application.IUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    IUserService userService;

    @GetMapping()
    public ResponseEntity<ApiResponse<UserRes>> getUserProfile() {
        return ResponseEntity.ok(userService.getUserProfileByUserId());
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Void>> updateUserProfile(@RequestBody UpdateUserReq request) {
        return ResponseEntity.ok(userService.updateUser(request));
    }

    @GetMapping("/confirm")
    public ResponseEntity<ApiResponse<UserRes>> confirmToken(@RequestParam String token) {
        return ResponseEntity.ok(userService.confirmToken(token));
    }
}
