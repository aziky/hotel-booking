package com.nls.userservice.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record ChangeRoleReq(
        @NotNull(message = "Target role is required")
        String targetRole
) {}