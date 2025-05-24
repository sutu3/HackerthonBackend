package com.example.hackerthon.Mapper;

import com.example.hackerthon.Dto.Request.ZaloPayCallback;
import org.mapstruct.Mapper;
import org.springframework.http.ResponseEntity;

@Mapper(componentModel = "spring")
public interface ZaloMapper {
    ZaloPayCallback toZaloPayCallback(ResponseEntity<?> response);
}
