package com.example.hackerthon.Dto.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RugPullResponse(
        String anomaly_score,
        int prediction_label,
        String prediction_message) {

}
