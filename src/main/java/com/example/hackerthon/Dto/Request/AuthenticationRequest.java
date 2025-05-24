package com.example.hackerthon.Dto.Request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthenticationRequest(
        String email,
        String password
) {
}
