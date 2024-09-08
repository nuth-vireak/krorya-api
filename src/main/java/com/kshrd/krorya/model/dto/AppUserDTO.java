package com.kshrd.krorya.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppUserDTO {
    private UUID userId;
    private String email;
    private String username;
    private Boolean isDeactivated;
    private String profileImage;
    private Integer followingsCount;
    private Integer followersCount;
    private String bio;
    private LocalDateTime createdDate;
}
