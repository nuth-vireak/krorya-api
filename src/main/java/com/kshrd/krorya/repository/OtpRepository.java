package com.kshrd.krorya.repository;

import com.kshrd.krorya.model.dto.OtpDTO;
import com.kshrd.krorya.model.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Repository
public interface OtpRepository extends JpaRepository<Otp, UUID> {

    // Find by OTP code
    Otp findByOtpCode(String otpCode);

    // Find OTP by user email
    @Query("SELECT o FROM Otp o WHERE o.appUser.email = :email")
    Otp findByEmail(String email);

    // Find OTP by user ID
    @Query("SELECT o FROM Otp o WHERE o.appUser.userId = :userId")
    Otp findByUserId(UUID userId);

    // Update OTP verification status
    @Transactional
    @Modifying
    @Query("UPDATE Otp o SET o.verify = true WHERE o.otpCode = :otpCode")
    void verifyOtp(String otpCode);

    // Update OTP verification status for forget password
    @Transactional
    @Modifying
    @Query("UPDATE Otp o SET o.isVerifiedForget = true WHERE o.otpCode = :otpCode")
    void verifyForgetPassword(String otpCode);

    // Update OTP details
    @Modifying
    @Transactional
    @Query("UPDATE Otp o SET o.otpCode = :#{#otp.otpCode}, o.expiresAt = :#{#otp.expiresAt}, o.verify = :#{#otp.verify} WHERE o.appUser.userId = :#{#otp.userId}")
    void updateOtp(@Param("otp") OtpDTO otpDTO);


    // Change user password
    @Transactional
    @Modifying
    @Query("UPDATE AppUser u SET u.password = :newPassword WHERE u.email = :email")
    void changePassword(String newPassword, String email);

    // Reset forget password verification flag
    @Transactional
    @Modifying
    @Query("UPDATE Otp o SET o.isVerifiedForget = false WHERE o.otpCode = :otpCode")
    void resetForgetPasswordVerification(String otpCode);

    @Transactional
    @Modifying
    @Query("SELECT o FROM Otp o WHERE o.appUser.userId = :userId")
    Otp findOtpByUserId(UUID userId);
}
