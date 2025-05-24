package com.example.hackerthon.Config;


import com.example.hackerthon.Enum.RoleUser;
import com.example.hackerthon.Model.Role;
import com.example.hackerthon.Model.User;
import com.example.hackerthon.Repo.RoleRepo;
import com.example.hackerthon.Repo.UserRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {


    PasswordEncoder passwordEncoder;
    @Bean
    ApplicationRunner applicationRunner(UserRepo userRepository, RoleRepo roleRepository){
        return args -> {
            // Initial data setup
            if(userRepository.findByUserName("admin").isEmpty()) {


                Role roleVip=roleRepository.save(Role.builder()
                        .name(RoleUser.Vip.name())
                        .description("Vip role has all permission")
                        .build());
                Role roleNormal=roleRepository.save(Role.builder()
                        .name(RoleUser.Normal.name())
                        .description("Normal role has all permission")
                        .build());
                Role roleAdmin=roleRepository.save(Role.builder()
                        .name(RoleUser.Admin.name())
                        .description("Admin role has all permission")
                        .build());
                HashSet<Role> roles=new HashSet<Role>();
                roles.add(roleAdmin);
                User user=User.builder()
                        .userName("admin")
                        .rawPassword("admin")
                        .password(passwordEncoder.encode("admin"))
                        .email("abc@gmail.com")
                        .phoneNumber("1234567890")
                        .roles(roles)
                        .build();
                userRepository.save(user);
            }
            log.warn("user admin created with default password username is admin");
        };
    }
}
