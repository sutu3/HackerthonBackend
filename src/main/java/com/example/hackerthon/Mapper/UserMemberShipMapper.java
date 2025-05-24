package com.example.hackerthon.Mapper;

import com.example.hackerthon.Dto.Request.UserRequest;
import com.example.hackerthon.Dto.Response.UserMemberShipResponse;
import com.example.hackerthon.Dto.Response.UserResponse;
import com.example.hackerthon.Model.User;
import com.example.hackerthon.Model.UserMemberShip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMemberShipMapper {
    UserMemberShipResponse toResponse(UserMemberShip entity);
}
