package com.example.hackerthon.Model;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    String idUser;
    @Column(columnDefinition = "VARCHAR(255) COMMENT 'tên cua nguoi dung'", nullable = false)
    String userName;
    @Column(columnDefinition = "VARCHAR(255) COMMENT 'pass cua nguoi dung'", nullable = false)
    String password;
    @Column(columnDefinition = "VARCHAR(255) COMMENT 'email cur nguoi dung'", nullable = false,unique = true)
    String email;
    @Column(columnDefinition = "VARCHAR(10) COMMENT 'phone cur nguoi dung'", nullable = false,unique = true)
    String phoneNumber;
    @ManyToMany
    Set<Role> roles;
    @Column(nullable = false)
    String rawPassword;
    @OneToMany(mappedBy="user")
    List<UserMemberShip> userMemberShips;
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    LocalDateTime createdAt;
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @UpdateTimestamp
    LocalDateTime updatedAt;

}
