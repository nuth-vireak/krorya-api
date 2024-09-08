package com.kshrd.krorya.service.serviceImp;

import com.kshrd.krorya.convert.AppUserConvertor;
import com.kshrd.krorya.exception.CustomBadRequestException;
import com.kshrd.krorya.exception.DuplicatedException;
import com.kshrd.krorya.exception.EmailAlreadyExistsException;
import com.kshrd.krorya.model.dto.AppUserDTO;
import com.kshrd.krorya.model.dto.OtpDTO;
import com.kshrd.krorya.model.entity.AppUser;
import com.kshrd.krorya.model.entity.CustomUserDetail;
import com.kshrd.krorya.model.request.AppUserRequest;
import com.kshrd.krorya.repository.AppUserRepository;
import com.kshrd.krorya.service.AppUserService;
import com.kshrd.krorya.service.OtpService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AppUserConvertor appUserConvertor;
    private final OtpService otpService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> userOptional = appUserRepository.findByEmail(username);

        AppUser user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found"));

        return new CustomUserDetail(user);

    }

    @Override
    public AppUserDTO createUser(AppUserRequest appUserRequest) {

        String email = appUserRequest.getEmail();

        if (appUserRepository.findByEmail(email).isPresent()){
            throw new EmailAlreadyExistsException("Email already exists in the system");
        }
        if (!appUserRequest.getPassword().equals(appUserRequest.getConfirmPassword())){
            throw new CustomBadRequestException("Password and confirm password do not match");
        }

        String encodedPassword = passwordEncoder.encode(appUserRequest.getPassword());

        AppUser appUser = AppUser.builder()
                .username(appUserRequest.getUsername())
                .email(appUserRequest.getEmail())
                .password(encodedPassword)
                .profileImage(appUserRequest.getProfileImage())
                .role("ROLE_USER")
                .isDeactivated(false)
                .followersCount(0)
                .followingsCount(0)
                .createdDate(LocalDateTime.now())
                .build();

        AppUser savedUser = appUserRepository.save(appUser);
        AppUserDTO appUserDTO = appUserConvertor.toDTO(savedUser);

        OtpDTO otpDTO = otpService.generateOtp(6, appUserConvertor.toEntity(appUserDTO));
        otpService.save(otpDTO);

        return appUserDTO;
    }
}
