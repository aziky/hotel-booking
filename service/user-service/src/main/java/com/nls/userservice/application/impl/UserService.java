package com.nls.userservice.application.impl;

import com.nls.common.dto.request.NotificationMessage;
import com.nls.common.dto.response.ApiResponse;
import com.nls.common.enumration.QueueName;
import com.nls.common.enumration.Role;
import com.nls.common.enumration.TypeEmail;
import com.nls.userservice.api.dto.request.CreateUserReq;
import com.nls.userservice.api.dto.request.LoginReq;
import com.nls.userservice.api.dto.request.UpdateUserReq;
import com.nls.userservice.api.dto.response.UserRes;
import com.nls.userservice.application.IUserService;
import com.nls.userservice.domain.entity.User;
import com.nls.userservice.domain.repository.UserRepository;
import com.nls.userservice.infrastructure.external.client.RedisService;
import com.nls.userservice.infrastructure.messaging.RabbitProducer;
import com.nls.userservice.shared.exceptions.EntityNotFoundException;
import com.nls.userservice.shared.mapper.UserMapper;
import com.nls.userservice.shared.utils.JwtUtil;
import com.nls.userservice.shared.utils.SecurityUtil;
import jakarta.persistence.EntityExistsException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class UserService implements IUserService {

    final UserRepository userRepository;
    final UserMapper userMapper;
    final JwtUtil jwtUtil;
    final PasswordEncoder passwordEncoder;
    final RabbitProducer rabbitProducer;
    final RedisService redisService;

    @Value("${fe-url.confirm-token}")
    String CONFIRM_URL;

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
    public ApiResponse<UserRes> getUserProfileByUserId() {
        try {
            UUID userId = SecurityUtil.getCurrentUserId();
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
    public ApiResponse<Void> createUser(CreateUserReq request) {
        try {
            log.info("Start create user with the request {}", request);
            if (userRepository.existsUserByEmail(request.email())) {
                throw new EntityExistsException("Already exist the user with this email " + request.email());
            }

            User user = userMapper.convertCreateUserReqToUser(request);
            user.setRole(Role.USER.name());
            user.setPasswordHash(passwordEncoder.encode(request.password()));

            String token = UUID.randomUUID().toString();
            redisService.save("REGISTER:" + token, user, Duration.ofMinutes(15));

            String confirmLink = CONFIRM_URL + token;
            Map<String, String> payload = new HashMap<>();
            payload.put("firstName", request.firstName());
            payload.put("confirmLink", confirmLink);

            NotificationMessage notificationMessage =  NotificationMessage.builder()
                    .to(request.email())
                    .type(TypeEmail.EMAIL_CONFIRM.name())
                    .payload(payload)
                    .build();

            log.info("Start send email to rabbit");
            rabbitProducer.sendEmailConfirm(
                    QueueName.EMAIL_CONFIRM_OTP.getExchangeName(),
                    QueueName.EMAIL_CONFIRM_OTP.getRoutingKey(),
                    notificationMessage);

            log.info("Send to rabbit successfully");
            return ApiResponse.created(null, "Registration successful. Check your email.");
        } catch (EntityExistsException e) {
            log.error(e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error at create user cause by {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }

    @Override
    public ApiResponse<Void> updateUser(UpdateUserReq request) {
        try {
            UUID userId = SecurityUtil.getCurrentUserId();
            log.info("Start update user with the request {}", request);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            userMapper.updateUserFromDto(request, user);
            userRepository.save(user);

            log.info("Update user successfully");
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("Error at update user cause by {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }

    @Override
    public ApiResponse<UserRes> confirmToken(String token) {
        try {
            log.info("Start confirm email with the token {}", token);
            User user = redisService.get("REGISTER:" + token, User.class);

            log.info("Value from the redis {}", user);
            user.setCreatedBy(user.getEmail());
            user.setUpdatedBy(user.getEmail());
            userRepository.save(user);

            UserRes userRes = userMapper.convertToUserRes(user);
            userRes = userRes.withToken(jwtUtil.generateToken(
                    String.valueOf(user.getId()),
                    user.getEmail(),
                    user.getRole()
            ));

            log.info("Create user successfully");
            return ApiResponse.ok(userRes);
        } catch (Exception e) {
            log.error("Error at confirm token cause by {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }
}
