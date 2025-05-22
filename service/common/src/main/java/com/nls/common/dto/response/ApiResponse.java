package com.nls.common.dto.response;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        int code,
        T data,
        String message
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(),  data, "OK");
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(HttpStatus.CREATED.value(),  data, "Created Successfully");
    }

    public static <T> ApiResponse<T> created() {
        return new ApiResponse<>(HttpStatus.CREATED.value(),  null, "Created Successfully");
    }

    public static <T> ApiResponse<T> accepted(T data) {
        return new ApiResponse<>(HttpStatus.ACCEPTED.value(), data, "Accepted");
    }

    public static <T> ApiResponse<T> badRequest(String message, T data) {
        return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), data, message);
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), null, message);
    }

    public static <T> ApiResponse<T> badGateway(String message, T data) {
        return new ApiResponse<>(HttpStatus.BAD_GATEWAY.value(), data, message);
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), null, message);
    }

    public static <T> ApiResponse<T> notFound(String message, T data) {
        return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), data, message);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(HttpStatus.OK.value(), null, "Success");
    }

    public static <T> ApiResponse<T> internalError() {
        return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),  null, "Error at server");
    }
}
