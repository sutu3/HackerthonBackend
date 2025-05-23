package com.example.hackerthon.Controller;

import com.example.hackerthon.Dto.Request.PredictRequest;
import com.example.hackerthon.Dto.Response.PredictResponse;
import com.example.hackerthon.Dto.Response.RugPullResponse;
import com.example.hackerthon.Service.HeliusService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/helius")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class HeliusController {

    HeliusService heliusService;

    @GetMapping("/transaction/{signature}")
    public RugPullResponse getTransaction(@PathVariable String signature) {
        return heliusService.getPredict(signature);
        // Spring WebFlux will handle subscribing to this Mono and sending the result as HTTP response.
    }
}