package com.example.hackerthon.Service;

import com.example.hackerthon.Dto.Response.UserMemberShipResponse;
import com.example.hackerthon.Dto.Response.UserResponse;
import com.example.hackerthon.Enum.RoleUser;
import com.example.hackerthon.Enum.Status;
import com.example.hackerthon.Exception.AppException;
import com.example.hackerthon.Exception.ErrorCode;
import com.example.hackerthon.Mapper.UserMemberShipMapper;
import com.example.hackerthon.Model.Role;
import com.example.hackerthon.Model.User;
import com.example.hackerthon.Model.UserMemberShip;
import com.example.hackerthon.Repo.RoleRepo;
import com.example.hackerthon.Repo.UserMemberShipRepo;
import com.example.hackerthon.Repo.UserRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class UserMemberShipService {
    private final UserMemberShipRepo userMemberShipRepo;
    private final UserMemberShipMapper userMemberShipMapper;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;

    @PreAuthorize("hasRole(Admin)")
    public List<UserMemberShipResponse> getall() {
        return userMemberShipRepo.findAll().stream()
                .map(userMemberShipMapper::toResponse).collect(Collectors.toList());
    }
    @PostAuthorize("returnObject.email==authentication.name")
    public UserMemberShipResponse UpdateMemberShip() {
        var context= SecurityContextHolder.getContext();
        String email=context.getAuthentication().getName();
        userMemberShipRepo.findByUser_EmailAndEndDateIsAfterAndStatus(email,LocalDate.now(), Status.Valid.toString())
                .orElseThrow(()->new AppException(ErrorCode.USER_MEMBERSHIP_EXIST));
        User user=userRepo.findByEmail(email)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        Role role=roleRepo.findByName(RoleUser.Normal.name())
                .orElseThrow(()->new AppException(ErrorCode.ROLE_NOT_FOUND));
        HashSet<Role> rolelist=new HashSet<Role>();
        rolelist.add(role);
        user.setRoles(rolelist);
        userRepo.save(user);
        return userMemberShipMapper.toResponse(userMemberShipRepo.save(UserMemberShip.builder()
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusMonths(1))
                        .status(Status.Valid)
                        .user(user)
                .build()));
    }
}
