package com.nls.userservice.api.dto.request;

public record LoginReq (
        String email,
        String password
) {
}
