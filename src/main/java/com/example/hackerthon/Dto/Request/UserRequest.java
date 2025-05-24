package com.example.hackerthon.Dto.Request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record UserRequest(
        String username,
        String password,
        String email,
        String phonenumber) {
}
