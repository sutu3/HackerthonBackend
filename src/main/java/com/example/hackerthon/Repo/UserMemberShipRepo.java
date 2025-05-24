package com.example.hackerthon.Repo;

import com.example.hackerthon.Model.UserMemberShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserMemberShipRepo extends JpaRepository<UserMemberShip,String> {
    Optional<UserMemberShip> findByUser_EmailAndEndDateIsAfterAndStatus(String email, LocalDate endtime, String status);
}
