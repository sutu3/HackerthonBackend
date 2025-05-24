package com.example.hackerthon.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
public class Role {
    @Id
    String name;
    @Column(columnDefinition = "VARCHAR(255) COMMENT 'mo ta cua role'", nullable = false,unique = true)
    String description;
}
