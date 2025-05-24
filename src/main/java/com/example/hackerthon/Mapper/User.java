package com.example.hackerthon.Mapper;

import com.example.hackerthon.Dto.Request.UserRequest;
import com.example.hackerthon.Dto.Response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface User {
    /*User toEntity(UserRequest request);
    UserResponse toResponse( )*/
}
