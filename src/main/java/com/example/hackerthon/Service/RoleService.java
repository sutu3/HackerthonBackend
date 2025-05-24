package com.example.hackerthon.Service;

import com.example.hackerthon.Dto.Request.RoleRequest;
import com.example.hackerthon.Dto.Response.RoleResponse;
import com.example.hackerthon.Exception.AppException;
import com.example.hackerthon.Exception.ErrorCode;
import com.example.hackerthon.Mapper.RoleMapper;
import com.example.hackerthon.Model.Role;
import com.example.hackerthon.Repo.RoleRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class RoleService {

    RoleRepo roleRepository;
    RoleMapper mapper;
    @PreAuthorize("hasRole('Admin')")
    public List<RoleResponse> getall() {
        return roleRepository.findAll().stream()
                .map(mapper::toResponse).collect(Collectors.toList());
    }
    @PreAuthorize("hasRole('Admin')")
    public RoleResponse PostRole(RoleRequest request){
        Role role=mapper.toEntity(request);
        if(roleRepository.existsRoleByName(request.name())){
            throw new AppException(ErrorCode.ROLE_IS_EXITED);
        }
        return mapper.toResponse(roleRepository.save(role));
    }

    public void deleteRole(String name) {
        roleRepository.findById(name)
                .orElseThrow(()->new AppException(ErrorCode.ROLE_NOT_FOUND));
        roleRepository.deleteById(name);
    }
}
