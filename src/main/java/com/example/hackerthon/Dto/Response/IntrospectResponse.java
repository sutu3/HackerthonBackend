package com.example.hackerthon.Dto.Response;

import lombok.Builder;

@Builder
public record IntrospectResponse(
        boolean valid
) {
}
