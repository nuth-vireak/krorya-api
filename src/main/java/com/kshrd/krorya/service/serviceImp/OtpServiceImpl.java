package com.kshrd.krorya.service.serviceImp;

import com.kshrd.krorya.convert.AppUserConvertor;
import com.kshrd.krorya.convert.OtpDTOConvertor;
import com.kshrd.krorya.exception.CustomBadRequestException;
import com.kshrd.krorya.exception.CustomNotFoundException;
import com.kshrd.krorya.model.dto.OtpDTO;
import com.kshrd.krorya.model.entity.AppUser;
import com.kshrd.krorya.model.entity.Otp;
import com.kshrd.krorya.model.request.PasswordRequest;
import com.kshrd.krorya.repository.OtpRepository;
import com.kshrd.krorya.service.OtpService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private static final Logger log = LoggerFactory.getLogger(OtpServiceImpl.class);
    private final OtpRepository otpRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final OtpDTOConvertor otpDTOConvertor;
    private final AppUserConvertor appUserConvertor;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public OtpDTO findById(Integer id) {
        return null;
    }

    @Override
    public void save(OtpDTO otpDTO) {
        log.info("OtpDTO in OtpServiceImpl: {}",otpDTO);
        Otp otp = otpDTOConvertor.toEntity(otpDTO);
        otpRepository.save(otp);
        log.info("Otp saved: {}", otp);
    }

    @Override
    public OtpDTO findByCode(String code) {

        if (!code.matches("\\d+")) {
            throw new CustomBadRequestException("Invalid OTP code. OTP must contain only digits.");
        }
        if (code.length() != 6) {
            throw new CustomBadRequestException("Invalid OTP code. OTP must be exactly 6 digits long.");
        }

        Otp otpCode = otpRepository.findByOtpCode(code);

        if (otpCode == null) {
            throw new CustomNotFoundException("Invalid OTP code.");
        }

        return otpDTOConvertor.toDTO(otpCode);
    }

    @Override
    public void uploadOtp(String otpCode) {
        // Implement as needed
    }

    @Override
    public void verify(String otpCode) {
        Otp byOtpCode = otpRepository.findByOtpCode(otpCode);
        if (byOtpCode.isVerify()){
            throw new CustomBadRequestException("This otp already verified!");
        }
        otpRepository.verifyOtp(otpCode);
    }

    @Override
    public void verifyForgetPassword(String otpCode) {
        Otp byOtpCode = otpRepository.findByOtpCode(otpCode);
        if (byOtpCode.isVerifiedForget()){
            throw new CustomBadRequestException("This otp already verified!");
        }
        otpRepository.verifyForgetPassword(otpCode);
    }

    @Override
    public void regenerateAndResendOTP(String email) throws MessagingException, IOException {
        Otp existingOtp = otpRepository.findByEmail(email);
        if (existingOtp == null) {
            throw new CustomNotFoundException("The email does not exist");
        }
        Integer otpLength = 6;
        OtpDTO newOtp = generateOtp(otpLength, existingOtp.getAppUser());
        existingOtp.setOtpCode(newOtp.getOtpCode());
        existingOtp.setExpiresAt(newOtp.getExpiresAt());
        otpRepository.updateOtp(otpDTOConvertor.toDTO(existingOtp));
        sendOtpEmail(email, newOtp.getOtpCode());
    }

    private void sendOtpEmail(String email, String otpCode) throws MessagingException {
//        log.info("Sending OTP email to: {}", email);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(fromEmail);
        helper.setTo(email);
        helper.setSubject("Your OTP for Email Verification");

        Context context = new Context();
        context.setVariable("otp", otpCode);
        String htmlContent = templateEngine.process("otp-mail", context);
        helper.setText(htmlContent, true);

        mailSender.send(message);
//        log.info("OTP email sent to: {}", email);
    }

    @Override
    public void resetPasswordByEmail(PasswordRequest passwordRequest, String email) {
        Otp existingOtp = otpRepository.findByEmail(email);
        if (!existingOtp.isVerifiedForget()) {
            throw new CustomBadRequestException("OTP didn't verify");
        }
        String newPassword = passwordEncoder.encode(passwordRequest.getPassword());
        otpRepository.changePassword(newPassword, email);
        otpRepository.resetForgetPasswordVerification(existingOtp.getOtpCode());
    }

    @Override
    public Otp findOtpByUserId(UUID userId) {
//        log.info("Fetching OTP for user: {}", userId);
        Otp otp = otpRepository.findOtpByUserId(userId);
//        System.out.println(otp + " after finding the user by id" );
//        log.info("Retrieved OTP: {}", otp);
        if (otp != null) {
//            log.info("AppUserDTO in OTP: {}", otp.getAppUser());
        }
        return otp;
    }

    @Override
    public OtpDTO generateOtp(Integer length, AppUser appUser) {
        final long expiryIntervalMinutes = 5; // 5 minutes
        String otpCode = generateOtpCode(length);

        // Use LocalDateTime for both issuedAt and expiresAt
        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime expiresAt = issuedAt.plusMinutes(expiryIntervalMinutes);

        return new OtpDTO(
                otpCode,
                issuedAt, // Set the issuedAt date
                expiresAt, // Set the expiry date
                false, // OTP not verified initially
                appUserConvertor.toDTO(appUser),
                appUser.getUserId(),
                false // Not verified for forgotten password scenario
        );
    }

    private String generateOtpCode(int length) {
        Random random = new Random();
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

}
