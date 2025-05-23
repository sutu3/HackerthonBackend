package com.example.hackerthon.Client;

import com.example.hackerthon.Dto.Response.PredictResponse;
import com.example.hackerthon.Dto.Response.RugPullResponse;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ai-model-service", url = "https://rug-pull-detection.onrender.com")
public interface AiModelClient {
    @PostMapping("/predict")
        // Giả sử endpoint là root của URL
    RugPullResponse predict(@RequestBody PredictResponse features);
}
