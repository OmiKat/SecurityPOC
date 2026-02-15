package com.omi.SeXurityPOC.mapper;

import com.omi.SeXurityPOC.pojos.User;
import com.omi.SeXurityPOC.pojos.UserRegistrationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserInfoMapper {

    private final PasswordEncoder passwordEncoder;
    public User convertToEntity(UserRegistrationDto userRegistrationDto) {
        User user = new User();
        user.setUserName(userRegistrationDto.userName());
        user.setEmail(userRegistrationDto.userEmail());
        user.setMobileNumber(userRegistrationDto.userMobileNo());
        user.setRoles(userRegistrationDto.userRole());
        user.setPassword(passwordEncoder.encode(userRegistrationDto.userPassword()));
        return user;
    }
}

