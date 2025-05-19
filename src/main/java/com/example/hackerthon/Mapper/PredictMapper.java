package com.example.hackerthon.Mapper;


import com.example.hackerthon.Dto.Request.PredictRequest;
import com.example.hackerthon.Dto.Response.PredictResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PredictMapper {
    PredictResponse toPredictResponse(PredictRequest request);
}
