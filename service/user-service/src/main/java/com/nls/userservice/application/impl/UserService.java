package com.nls.userservice.application.impl;

import com.nls.userservice.api.dto.request.LoginReq;
import com.nls.userservice.api.dto.response.UserRes;
import com.nls.userservice.application.IUserService;
import com.nls.userservice.domain.entity.User;
import com.nls.userservice.domain.repository.UserRepository;
import com.nls.userservice.shared.base.ApiResponse;
import com.nls.userservice.shared.exceptions.EntityNotFoundException;
import com.nls.userservice.shared.mapper.UserMapper;
import com.nls.userservice.shared.utils.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService implements IUserService {

    UserRepository userRepository;
    UserMapper userMapper;
    JwtUtil jwtUtil;


    @Override
    public ApiResponse<UserRes> login(LoginReq loginReq) {
        try {
            log.info("Start handle login");
            User user = userRepository.findByEmailAndPasswordHash(loginReq.email(), loginReq.password())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            UserRes userRes = userMapper.convertToUserRes(user);
            userRes = userRes.withToken(jwtUtil.generateToken(String.valueOf(user.getId()), user.getEmail(), user.getRole()));
            return ApiResponse.ok(userRes);
        } catch (EntityNotFoundException e) {
            log.warn("Login failed cause by  {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error at login cause by {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }

    @Override
    public ApiResponse<UserRes> getUserProfileByUserId(UUID userId) {
        try {
            log.info("start handle at get user profile with request {}", userId);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with userId " + userId));
            return ApiResponse.ok(userMapper.convertToUserRes(user));
        } catch (EntityNotFoundException e ) {
            log.warn("Get user profile failed cause by {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error at get user profile by user id cause by {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }
}
