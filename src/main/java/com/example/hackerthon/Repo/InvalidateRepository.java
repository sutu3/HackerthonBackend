package com.example.hackerthon.Repo;

import com.example.hackerthon.Model.InvalidateToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidateRepository extends JpaRepository<InvalidateToken,String> {
}
