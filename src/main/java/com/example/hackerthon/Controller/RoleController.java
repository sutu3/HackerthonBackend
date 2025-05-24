package com.example.hackerthon.Controller;

import com.example.hackerthon.Dto.Request.RoleRequest;
import com.example.hackerthon.Dto.Request.UserRequest;
import com.example.hackerthon.Dto.Response.ApiResponse;
import com.example.hackerthon.Dto.Response.RoleResponse;
import com.example.hackerthon.Dto.Response.UserResponse;
import com.example.hackerthon.Service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class RoleController {
    RoleService roleService;
    @GetMapping
    public ApiResponse<List<RoleResponse>> getall(){
        return ApiResponse.<List<RoleResponse>>builder()
                .Result(roleService.getall())
                .code(0)
                .success(true)
                .message("Completed")
                .build();
    }

    @PostMapping
    public ApiResponse<RoleResponse> postUser(@RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .Result(roleService.PostRole(request))
                .code(0)
                .message("Completed")
                .success(true)
                .build();
    }
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteUser(@PathVariable String id){
        roleService.deleteRole(id);
        return ApiResponse.<String>builder()
                .Result("Deleted Success")
                .code(0)
                .message("Completed")
                .success(true)
                .build();
    }
}
