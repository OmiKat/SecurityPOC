package com.omi.SeXurityPOC.pojos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record UserRegistrationDto(

        @NotEmpty(message = "User Name should not be empty")
        String userName,

        String userMobileNo,

        @NotEmpty(message = "Email cannot be empty")
        @Email(message = "invalid email format")
        String userEmail,

        @NotEmpty(message = "Password cannot be empty")
        String userPassword,

        @NotEmpty(message = "Roles cannot be empty")
        String userRole
) { }
