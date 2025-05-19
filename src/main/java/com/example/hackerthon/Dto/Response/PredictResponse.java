package com.example.hackerthon.Dto.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.Map;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PredictResponse(
         String address,
         double probability,
         Map<String, Object> features
) {
}
