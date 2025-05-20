package com.example.hackerthon.Controller;

import com.example.hackerthon.Dto.Request.PredictRequest;
import com.example.hackerthon.Dto.Response.PredictResponse;
import com.example.hackerthon.Service.HeliusService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/helius")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class HeliusController {
    HeliusService heliusService;
    @GetMapping("/transaction/{signature}")
    public PredictResponse getTransaction(@PathVariable String signature) throws Exception {
        return heliusService.getPredict(signature);
    }
}
