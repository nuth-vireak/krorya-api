package com.kshrd.krorya.service.serviceImplementation;

import com.kshrd.krorya.convert.AppUserConvertor;
import com.kshrd.krorya.exception.CustomBadRequestException;
import com.kshrd.krorya.exception.CustomNotFoundException;
import com.kshrd.krorya.exception.DuplicatedException;
import com.kshrd.krorya.exception.SearchNotFoundException;
import com.kshrd.krorya.model.dto.AppUserDTO;
import com.kshrd.krorya.model.dto.OtpDTO;
import com.kshrd.krorya.model.entity.AppUser;
import com.kshrd.krorya.model.entity.CustomUserDetail;
import com.kshrd.krorya.model.entity.Follow;
import com.kshrd.krorya.model.request.AppUserRequest;
import com.kshrd.krorya.model.request.GoogleUserRequest;
import com.kshrd.krorya.model.request.ResetPasswordRequest;
import com.kshrd.krorya.model.request.UserRequest;
import com.kshrd.krorya.repository.AppUserRepository;
import com.kshrd.krorya.repository.FollowRepository;
import com.kshrd.krorya.service.AppUserService;
import com.kshrd.krorya.service.OtpService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AppUserConvertor appUserConvertor;
    private final OtpService otpService;
//    private final EmailService emailService;
    private final FollowRepository followRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByEmail(username);
        return new CustomUserDetail(user);

    }

    @Override
    public CustomUserDetail createUserFromGoogle(GoogleUserRequest googleUserRequest) {
        googleUserRequest.setPassword(passwordEncoder.encode(googleUserRequest.getPassword()));
        // Check if user already exists by email
        if (appUserRepository.findByEmail(googleUserRequest.getEmail()) != null){
            throw new DuplicatedException("Email already exists");
        }
        return appUserConvertor.toCustomUserDetail(appUserRepository.registerWithGoogle(googleUserRequest));
    }

    @Override
    public AppUserDTO updateUserById(UUID userId, UserRequest userRequest) {
        return appUserConvertor.toDTO(appUserRepository.updateUserById(userId, userRequest));
    }

    @Override
    public void changeUserPassword(ResetPasswordRequest resetPasswordRequest, UUID userId) {
        AppUser appUser = appUserRepository.getUserById(userId);
        boolean isMatch = passwordEncoder.matches(resetPasswordRequest.getOldPassword(), appUser.getPassword());
        if (!isMatch){
            throw new CustomBadRequestException("The old password is invalid!");
        }
        String newPassword = passwordEncoder.encode(resetPasswordRequest.getNewPassword());
        appUserRepository.resetPassword(newPassword, userId);
    }

    @Override
    public AppUserDTO findUserByUserId(UUID currentUserId, UUID userId) {
        AppUser appUser = appUserRepository.getUserById(userId);
        if(appUser == null){
            throw new CustomNotFoundException("user id "+ userId +" doesn't exist");
        }
        AppUserDTO appUserDTO = appUserConvertor.toDTO(appUser);
        if (currentUserId == null){
                appUserDTO.setIsFollowingByCurrentUser(false);
        }
        else{
            Follow follow = followRepository.getFollowingByFollower(currentUserId, userId);
            if (follow != null){
                appUserDTO.setIsFollowingByCurrentUser(true);
            }
        }





        return appUserDTO;
    }

    @Override
    public AppUserDTO getCurrentUserInfo() {
        UUID currentUserId = getCurrentUserId();
        return appUserConvertor.toDTO(appUserRepository.getUserById(currentUserId));
    }

    @Override
    public List<AppUserDTO> getAllUsers() {
        return appUserConvertor.toListDTO(appUserRepository.findAllUsers());
    }

    @Override
    public AppUser findUserByEmail(String email) {
        AppUser appUserRepositoryByEmail = appUserRepository.findByEmail(email);

        if (appUserRepositoryByEmail == null) {
            throw new SearchNotFoundException("Email: " + email + " not found");
        }

        return appUserRepositoryByEmail;
    }

    @Override
    public AppUserDTO createUser(AppUserRequest appUserRequest) throws MessagingException, IOException {

        String email = appUserRequest.getEmail();

        if (appUserRepository.findByEmail(email) != null){
            throw new DuplicatedException("Email already exists");
        }
        if (!appUserRequest.getPassword().equals(appUserRequest.getConfirmPassword())){
            throw new CustomBadRequestException("Password and confirm password do not match");
        }

        String encodedPassword = passwordEncoder.encode(appUserRequest.getPassword());
        appUserRequest.setPassword(encodedPassword);
        AppUser appUser = appUserRepository.saveUser(appUserRequest);

        AppUserDTO appUserDTO = appUserConvertor.toDTO(appUser);

        OtpDTO otpDTO = otpService.generateOtp(6, appUserConvertor.toEntity(appUserDTO));
        otpService.save(otpDTO);

//        String otpGenerated = emailService.sendEmail(appUserRequest.getEmail(), otpDTO.getOtpCode());

        return appUserDTO;
    }

    private UUID getCurrentUserId() {
        CustomUserDetail userDetails = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getAppUser().getUserId();
    }
}
