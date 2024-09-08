package com.kshrd.krorya.controller;

import com.kshrd.krorya.model.dto.AppUserDTO;
import com.kshrd.krorya.model.request.AppUserRequest;
import com.kshrd.krorya.model.response.ApiResponse;
import com.kshrd.krorya.service.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
@Slf4j
@Tag(name = "Auth Controller", description = "Endpoints for managing Authentication")
public class AuthController {

    private final AppUserService appUserService;

    @Operation(
            summary = "User Registration",
            description = "Register a new user and send OTP for verification",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            {
                                "username": "john_doe",
                                "email": "john.doe@example.com",
                                "password": "Password123",
                                "confirmPassword": "Password123",
                                "profileImage": "http://example.com/profile.jpg"
                            }
                            """)
                    )
            )
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AppUserRequest appUserRequest) throws MessagingException, IOException {

        AppUserDTO appUserDTO = appUserService.createUser(appUserRequest);

        ApiResponse<AppUserDTO> response = ApiResponse.<AppUserDTO>builder()
                .message("Successfully Registered")
                .status(HttpStatus.CREATED)
                .code(201)
                .payload(appUserDTO)
                .localDateTime(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
