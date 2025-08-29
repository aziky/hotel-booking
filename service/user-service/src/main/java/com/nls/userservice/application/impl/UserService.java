package com.nls.userservice.application.impl;

import com.nls.common.dto.request.NotificationMessage;
import com.nls.common.dto.response.ApiResponse;
import com.nls.common.enumration.QueueName;
import com.nls.common.enumration.Role;
import com.nls.common.enumration.TypeEmail;
import com.nls.userservice.api.dto.request.*;
import com.nls.common.dto.response.UserRes;
import com.nls.userservice.application.IUserService;
import com.nls.userservice.domain.entity.User;
import com.nls.userservice.domain.repository.UserRepository;
import com.nls.userservice.infrastructure.external.client.RedisService;
import com.nls.userservice.infrastructure.messaging.RabbitProducer;
import com.nls.userservice.infrastructure.properties.FeProperties;
import com.nls.userservice.shared.exceptions.EntityNotFoundException;
import com.nls.userservice.shared.mapper.UserMapper;
import com.nls.userservice.shared.utils.AuditContext;
import com.nls.userservice.shared.utils.JwtUtil;
import com.nls.userservice.shared.utils.SecurityUtil;
import jakarta.persistence.EntityExistsException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
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
    final AuditContext auditContext;
    final FeProperties feProperties;

    @Override
    public ApiResponse<UserRes> login(LoginReq loginReq) {
        try {
            log.info("Start handle login with email {}", loginReq.email());
            User user = userRepository.findByEmail(loginReq.email())
                    .orElseThrow(() -> new EntityNotFoundException("User nof found"));


            if (!passwordEncoder.matches(loginReq.password(), user.getPasswordHash())) {
                log.warn("Login failed: incorrect password for email {}", loginReq.email());
                throw new AccessDeniedException("Invalid email or password");
            }

            UserRes userRes = userMapper.convertToUserRes(user);
            userRes = userRes.withToken(jwtUtil.generateToken(
                    String.valueOf(user.getId()),
                    user.getEmail(),
                    user.getRole()));
            log.info("Login successfully with {}", userRes);
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
        } catch (EntityNotFoundException e) {
            log.warn("Get user profile failed cause by {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error at get user profile by user id cause by {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }

    @Transactional
    @Override
    public ApiResponse<Void> createUser(CreateUserReq request) {
        try {
            log.info("Start create user with the request");
            if (userRepository.existsUserByEmail(request.email())) {
                throw new EntityExistsException("Already exist the user with this email " + request.email());
            }

            User user = userMapper.convertCreateUserReqToUser(request);
            user.setRole(Role.USER.name());
            user.setPasswordHash(passwordEncoder.encode(request.password()));

            String token = UUID.randomUUID().toString();
            redisService.save("REGISTER:" + token, user, Duration.ofMinutes(15));

            String confirmLink = feProperties.host() + feProperties.confirmToken() + token;
            Map<String, String> payload = new HashMap<>();
            payload.put("firstName", request.firstName());
            payload.put("confirmLink", confirmLink);

            NotificationMessage notificationMessage = NotificationMessage.builder()
                    .to(request.email())
                    .type(TypeEmail.EMAIL_CONFIRM.name())
                    .payload(payload)
                    .build();

            log.info("Start send email to rabbit");
            rabbitProducer.sendEmail(
                    QueueName.EMAIL_CONFIRM_OTP.getExchangeName(),
                    QueueName.EMAIL_CONFIRM_OTP.getRoutingKey(),
                    notificationMessage);

            log.info("Send to rabbit successfully for create user");
            return ApiResponse.created(null, "Registration successful. Check your email.");
        } catch (EntityExistsException e) {
            log.error(e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error at create user cause by {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }

    @Transactional
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

    @Transactional
    @Override
    public ApiResponse<UserRes> confirmToken(String token) {
        try {
            log.info("Start confirm email with the token {}", token);
            User user = redisService.get("REGISTER:" + token, User.class);

            log.info("Value from the redis {}", user);
            auditContext.setTemporaryUser(user.getEmail());
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

    @Override
    public ApiResponse<Void> forgetPassword(ForgetPasswordReq request) {
        try {
            log.info("Start handle forget password request {}", request);
            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new EntityExistsException("User not found with user email " + request.email()));

            String token = UUID.randomUUID().toString();
            Map<String, String> payload = new HashMap<>();
            payload.put("resetLink", feProperties.host() + feProperties.forgetPassword() + token);
            payload.put("firstName", user.getFirstName());

            auditContext.setTemporaryUser(request.email());

            log.info("Start saving into redis with {}", token);
            redisService.save("RESET-PASSWORD:" + token, user.getId(), Duration.ofMinutes(15));

            NotificationMessage notificationMessage = NotificationMessage.builder()
                    .to(request.email())
                    .type(TypeEmail.EMAIL_FORGET_PASSWORD.name())
                    .payload(payload)
                    .build();

            rabbitProducer.sendEmail(
                    QueueName.EMAIL_FORGET_PASSWORD.getExchangeName(),
                    QueueName.EMAIL_FORGET_PASSWORD.getRoutingKey(),
                    notificationMessage
            );

            log.info("Send to rabbit successfully for reset password");
            return ApiResponse.ok(null, "Send email to reset password");
        } catch (EntityNotFoundException e) {
            log.error("Forget password failed cause by {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error at forget password cause by {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }

    @Transactional
    @Override
    public ApiResponse<UserRes> resetPassword(ResetPasswordReq request) {
        try {
            log.info("Start handle reset password");
            String userId = redisService.get("RESET-PASSWORD:" + request.token(), String.class);

            if (userId == null) {
                throw new IllegalArgumentException("Reset token is invalid or has expired.");
            }

            User user = userRepository.findById(UUID.fromString(userId)).get();
            auditContext.setTemporaryUser(user.getEmail());
            user.setPasswordHash(passwordEncoder.encode(request.password()));
            userRepository.save(user);

            log.info("Reset password successfully");
            UserRes userRes = userMapper.convertToUserRes(user);
            userRes = userRes.withToken(jwtUtil.generateToken(user.getId().toString(), user.getEmail(), user.getRole()));

            return ApiResponse.ok(userRes);
        } catch (IllegalArgumentException e) {
            log.error("Reset password failed cause by {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        }
        catch (Exception e) {
            log.error("Error at reset password cause by {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }
    @Transactional
    @Override
    public ApiResponse<UserRes> changeUserRole() {
        try {
            UUID userId = SecurityUtil.getCurrentUserId();
            log.info("Start change user role to HOST for user: {}", userId);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            // Validate current role
            if (!Role.USER.name().equals(user.getRole())) {
                log.warn("Role change denied: User {} already has role {}", userId, user.getRole());
                return ApiResponse.badRequest("Only users with USER role can change to HOST role");
            }

            // Update user role to HOST (no need to validate target role since it's fixed)
            user.setRole(Role.HOST.name());
            userRepository.save(user);

            // Generate new token with updated role
            UserRes userRes = userMapper.convertToUserRes(user);
            userRes = userRes.withToken(jwtUtil.generateToken(
                    String.valueOf(user.getId()),
                    user.getEmail(),
                    user.getRole()
            ));

            log.info("User role changed successfully from USER to HOST for user: {}", userId);
            return ApiResponse.ok(userRes, "Role changed successfully to HOST");

        } catch (EntityNotFoundException e) {
            log.warn("Role change failed: {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error at change user role: {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }
    @Override
    public ApiResponse<Long> countUsersByRole(String role) {
        try {
            log.info("Counting users with role: {}", role);

            // Validate role
            if (role == null || role.trim().isEmpty()) {
                log.warn("Invalid role provided: {}", role);
                return ApiResponse.badRequest("Role cannot be null or empty");
            }

            // Validate that it's a valid role
            try {
                Role.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role value: {}", role);
                return ApiResponse.badRequest("Invalid role. Valid roles are: USER, HOST, ADMIN");
            }

            Long userCount = userRepository.countByRole(role.toUpperCase());

            log.info("Found {} users with role: {}", userCount, role);
            return ApiResponse.ok(userCount);

        } catch (Exception e) {
            log.error("Error counting users by role {}: {}", role, e.getMessage(), e);
            return ApiResponse.internalError();
        }
    }
    @Override
    public ApiResponse<List<UserRes>> getUsersByRole(String role) {
        try {
            List<User> users = userRepository.findByRole(role);
            List<UserRes> userResList = users.stream()
                    .map(userMapper::convertToUserRes)
                    .toList();
            return ApiResponse.ok(userResList);
        } catch (Exception e) {
            log.error("Error getting users by role {}: {}", role, e.getMessage());
            return ApiResponse.internalError();
        }
    }
}
