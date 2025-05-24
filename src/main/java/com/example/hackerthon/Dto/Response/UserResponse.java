package com.example.hackerthon.Dto.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record UserResponse(
        String userid,
        String username,
        String password,
        String email,
        Set<RoleResponse> role,
        String phonenumber) {
}
