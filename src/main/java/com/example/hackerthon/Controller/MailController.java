package com.example.hackerthon.Controller;

import com.example.hackerthon.Dto.Request.NotificationRequest;
import com.example.hackerthon.Dto.Response.ApiResponse;
import com.example.hackerthon.Dto.Response.RoleResponse;
import com.example.hackerthon.Service.JavaMailService;
import com.example.hackerthon.Service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mail")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class MailController {
    JavaMailService mailService;
    @GetMapping
    public ApiResponse<String> getall(@RequestBody NotificationRequest request){
        mailService.forgetPassword(request);
        return ApiResponse.<String>builder()
                .Result("Mail send Success")
                .code(0)
                .success(true)
                .message("Completed")
                .build();
    }

}
