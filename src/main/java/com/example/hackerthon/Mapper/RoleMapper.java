package com.example.hackerthon.Mapper;

import com.example.hackerthon.Dto.Request.RoleRequest;
import com.example.hackerthon.Dto.Response.RoleResponse;
import com.example.hackerthon.Model.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toEntity(RoleRequest request);
    RoleResponse toResponse(Role entity);
}
