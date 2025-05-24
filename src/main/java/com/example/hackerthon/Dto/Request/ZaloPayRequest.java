package com.example.hackerthon.Dto.Request;

import lombok.Builder;

@Builder
public record ZaloPayRequest(
        String amount,
        String orderInfo
) {
}
