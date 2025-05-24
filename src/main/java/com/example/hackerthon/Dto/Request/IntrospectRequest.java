package com.example.hackerthon.Dto.Request;

import lombok.Builder;

@Builder
public record IntrospectRequest(
        String token
) {
}
