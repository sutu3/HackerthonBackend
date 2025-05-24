package com.example.hackerthon.Dto.Response;

import com.example.hackerthon.Model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record UserMemberShipResponse(
        String id,
        LocalDate startDate,
        LocalDate endDate,
        UserResponse user
) {
}
