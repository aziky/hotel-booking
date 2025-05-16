package com.nls.userservice.application.impl;

import com.nls.userservice.api.dto.request.CreateUseReq;
import com.nls.userservice.api.dto.request.LoginReq;
import com.nls.userservice.api.dto.response.UserRes;
import com.nls.userservice.application.IUserService;
import com.nls.userservice.domain.entity.User;
import com.nls.userservice.domain.enumeration.Role;
import com.nls.userservice.domain.repository.UserRepository;
import com.nls.userservice.shared.base.ApiResponse;
import com.nls.userservice.shared.exceptions.EntityNotFoundException;
import com.nls.userservice.shared.mapper.UserMapper;
import com.nls.userservice.shared.utils.JwtUtil;
import jakarta.persistence.EntityExistsException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    PasswordEncoder passwordEncoder;


    @Override
    public ApiResponse<UserRes> login(LoginReq loginReq) {
        try {
            log.info("Start handle login with email {}", loginReq.email());
            User user = userRepository.findByEmail(loginReq.email())
                    .orElseThrow(() -> new EntityNotFoundException("User nof found"));


            if (!passwordEncoder.matches(loginReq.password(), user.getPasswordHash())) {
                log.warn("Login failed: incorrect password for email {}", loginReq.email());
                return ApiResponse.unauthorized("Invalid email or password");
            }

            UserRes userRes = userMapper.convertToUserRes(user);
            userRes = userRes.withToken(jwtUtil.generateToken(
                    String.valueOf(user.getId()),
                    user.getEmail(),
                    user.getRole()));

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
            log.info("Start handle at get user profile with request {}", userId);
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

    @Override
    public ApiResponse<Void> createUser(CreateUseReq request) {
        try {
            log.info("Start create user with the request {}", request);
            if (userRepository.existsUserByEmail(request.email())) {
                throw new EntityExistsException("Already exist the user with this email " + request.email());
            }

            User user = userMapper.convertCreateUserReqToUser(request);
            user.setRole(Role.USER.name());
            user.setPasswordHash(passwordEncoder.encode(request.password()));

            userRepository.save(user);
            log.info("save user successfully");
            return ApiResponse.created();
        } catch (EntityExistsException e) {
            log.error(e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error at create user cause by {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }
}
