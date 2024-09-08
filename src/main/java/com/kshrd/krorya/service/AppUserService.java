package com.kshrd.krorya.service;

import com.kshrd.krorya.model.dto.AppUserDTO;
import com.kshrd.krorya.model.request.AppUserRequest;
import jakarta.mail.MessagingException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;

public interface AppUserService extends UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    AppUserDTO createUser(AppUserRequest appUserRequest) throws MessagingException, IOException;
}
