package com.example.hackerthon.Controller;

import com.example.hackerthon.Dto.Request.UserRequest;
import com.example.hackerthon.Dto.Response.ApiResponse;
import com.example.hackerthon.Dto.Response.UserResponse;
import com.example.hackerthon.Service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserController {
    UserService userService;
    @GetMapping
    public ApiResponse<List<UserResponse>> getall(){
        return ApiResponse.<List<UserResponse>>builder()
                .Result(userService.getall())
                .code(0)
                .success(true)
                .message("Completed")
                .build();
    }

    @PostMapping("/create/user")
    public ApiResponse<UserResponse> posetUser(@RequestBody UserRequest request){
        return ApiResponse.<UserResponse>builder()
                .Result(userService.PostUser(request))
                .code(0)
                .success(true)
                .message("Completed")
                .build();
    }
    @GetMapping("/myinfor")
    public ApiResponse<UserResponse> getmyinfor(){
        return ApiResponse.<UserResponse>builder()
                .Result(userService.getmyInfor())
                .code(0)
                .success(true)
                .message("Completed")
                .build();
    }
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getbyId(@PathVariable String id) {
        return ApiResponse.<UserResponse>builder()
                .Result(userService.getbyId(id))
                .code(0)
                .success(true)
                .message("Completed")
                .build();
    }
    @PostMapping("/update")
    public ApiResponse<UserResponse> postUser(@RequestBody UserRequest request) {
        return ApiResponse.<UserResponse>builder()
                .Result(userService.PostUser(request))
                .code(0)
                .message("Completed")
                .success(true)
                .build();
    }
}
