package com.nls.userservice.api.dto.response;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record UserRes(
        String name,
        String email,
        String token
) {
}
