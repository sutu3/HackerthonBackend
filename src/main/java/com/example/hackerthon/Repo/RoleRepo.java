package com.example.hackerthon.Repo;

import com.example.hackerthon.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role,String> {
    boolean existsRoleByName(String name);

    Optional<Role> findByName(String name);
}
