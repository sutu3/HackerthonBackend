package com.example.hackerthon.Dto.Request;

import org.springframework.cloud.client.loadbalancer.RequestData;

public record ZaloPayCallback(
        RequestData request_data,
        String response_data
) {
}
