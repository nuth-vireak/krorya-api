package com.kshrd.krorya.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUserRequest {

    @NotBlank(message = "Username is required")
    @NotEmpty(message = "Username should not be empty")
    private String username;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @NotEmpty(message = "Email should not be empty")
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "Password must be at least 8 characters long, with at least one letter and one number")
    @NotBlank(message = "Password is required")
    @NotEmpty(message = "Password should not be empty")
    private String password;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "Confirm Password must be at least 8 characters long, with at least one letter and one number")
    @NotBlank(message = "Confirm Password is required")
    @NotEmpty(message = "Confirm Password should not be empty")
    private String confirmPassword;

    @NotBlank(message = "Profile picture is required")
    @NotEmpty(message = "Profile picture should not be empty")
    private String profileImage;
}
