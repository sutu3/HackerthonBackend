package com.example.hackerthon.Mapper;

import com.example.hackerthon.Dto.Request.UserRequest;
import com.example.hackerthon.Dto.Response.UserResponse;
import com.example.hackerthon.Model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "username", target = "userName")
    @Mapping(source = "phonenumber", target = "phoneNumber")
    User toEntity(UserRequest request);
    @Mapping(source = "roles",target = "role")
    @Mapping(source = "userName", target = "username")
    @Mapping(source = "phoneNumber", target = "phonenumber")
    UserResponse toResponse(User entity);
}
