package com.kshrd.krorya.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
public class OtpDTO {
    private static final Logger log = LoggerFactory.getLogger(OtpDTO.class);
    private String otpCode;
    private LocalDateTime issuedDate;
    private LocalDateTime expiresAt;
    private boolean verify;
    private AppUserDTO appUserDTO;
    private UUID userId;
    private boolean isVerifiedForget;

    public OtpDTO(String otpCode, LocalDateTime issuedDate, LocalDateTime expiresAt, boolean verify, AppUserDTO appUserDTO, UUID userId, boolean isVerifiedForget) {
        this.otpCode = otpCode;
        this.issuedDate = issuedDate;
        this.expiresAt = expiresAt;
        this.verify = verify;
        this.appUserDTO = appUserDTO;
        this.userId = userId;
        this.isVerifiedForget = isVerifiedForget;
    }



}