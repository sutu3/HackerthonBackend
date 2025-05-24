package com.example.hackerthon.Repo;

import com.example.hackerthon.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,String> {
    boolean existsUserByEmail(String email);
    Optional<User> findByUserName(String name);
    Optional<User> findByEmail(String email);
}
