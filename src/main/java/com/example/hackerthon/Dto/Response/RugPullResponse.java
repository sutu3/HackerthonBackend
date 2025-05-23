package com.example.hackerthon.Dto.Response;

import lombok.Builder;

@Builder
public record RugPullResponse(
        String anomaly_score,
        int prediction_label,
        String prediction_message) {

}
