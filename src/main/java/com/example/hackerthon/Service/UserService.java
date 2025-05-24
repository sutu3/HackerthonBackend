package com.example.hackerthon.Service;

import com.example.hackerthon.Dto.Request.UserRequest;
import com.example.hackerthon.Dto.Response.UserResponse;
import com.example.hackerthon.Enum.RoleUser;
import com.example.hackerthon.Exception.AppException;
import com.example.hackerthon.Exception.ErrorCode;
import com.example.hackerthon.Mapper.UserMapper;
import com.example.hackerthon.Model.Role;
import com.example.hackerthon.Model.User;
import com.example.hackerthon.Repo.RoleRepo;
import com.example.hackerthon.Repo.UserRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class UserService {
    UserMapper mapper;
    UserRepo UserRepo;
    PasswordEncoder passwordEncoder;
    RoleRepo roleRepo;

    @PostAuthorize("returnObject.email==authentication.name")
    public UserResponse getmyInfor() {
        var context= SecurityContextHolder.getContext();
        String userName=context.getAuthentication().getName();
        log.info(userName);
        return mapper.toResponse(UserRepo.findByEmail(userName)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND)));
    }
    public List<UserResponse> getall() {
        return UserRepo.findAll().stream()
                .map(mapper::toResponse).collect(Collectors.toList());
    }

    


    public UserResponse getbyId(String id) {
        return mapper.toResponse(UserRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }

/*
    @PreAuthorize("hasRole('ADMIN')")
*/
    public UserResponse PostUser(UserRequest request) {


        if (UserRepo.existsUserByEmail(request.email())) {
            throw new AppException(ErrorCode.EMAIL_IS_EXITED);
        }
        User user = mapper.toEntity(request);
        Role role=roleRepo.findByName(RoleUser.Normal.name())
                .orElseThrow(()->new AppException(ErrorCode.ROLE_NOT_FOUND));
        HashSet<Role> rolelist=new HashSet<Role>();
        rolelist.add(role);
        return mapper.toResponse(UserRepo
                .save(user.builder()
                        .userName(request.username())
                        .email(request.email())
                        .phoneNumber(request.phonenumber())
                        .password(passwordEncoder.encode(request.password()))
                        .rawPassword(request.password())
                        .createdAt(LocalDateTime.now())
                        .roles(rolelist)
                        .build()));
    }

/*
    public UserResponse putUser(String id, User_Update update) {
        User userupdate = UserRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        mapper.updateUser(userupdate, update);
        userupdate.setUpdateAt(LocalDateTime.now());
        return mapper.toUserResponse(UserRepo
                .save(userupdate));
    }
*/
   /* public UserResponse putUser(String id, UserRequest request) {
        User userupdate = UserRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userupdate.setUpdateAt(LocalDateTime.now());
        return mapper.toUserResponse(UserRepo
                .save(userupdate));
    }*/
   /* public void deleteUser(String id){
        User user=UserRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        UserRepo
                .save(user.builder()
                        .deleteAt(LocalDateTime.now())
                        .isDeleted(true)
                        .build());
    }*/


}
