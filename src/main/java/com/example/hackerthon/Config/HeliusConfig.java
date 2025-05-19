package com.example.hackerthon.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HeliusConfig {

    @Value("${helius.api.key}")
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }
}

