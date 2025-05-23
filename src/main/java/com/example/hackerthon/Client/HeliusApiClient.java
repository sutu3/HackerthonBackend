package com.example.hackerthon.Client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "helius-api-service", url = "https://api.helius.xyz")
public interface HeliusApiClient {
    @GetMapping("/v0/addresses/{address}/transactions")
    JsonNode getTransactions(@PathVariable("address") String address, @RequestParam("api-key") String apiKey);
}
