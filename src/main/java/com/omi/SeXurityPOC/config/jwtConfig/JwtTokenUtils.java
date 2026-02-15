package com.omi.SeXurityPOC.config.jwtConfig;

import com.omi.SeXurityPOC.config.userConfig.UserConfig;
import com.omi.SeXurityPOC.config.userConfig.UserInfoConfig;
import com.omi.SeXurityPOC.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {



    public String getUserName(Jwt jwt){
        return jwt.getSubject();
    }

    public boolean isValid(Jwt jwt , UserDetails userDetails){
        final String userName = getUserName(jwt);
        boolean isToKenExpired = getIfTokenIsExpired(jwt);
        boolean isTokenUserSameAsDataBase = userName.equals(userDetails.getUsername());
        return !isToKenExpired && isTokenUserSameAsDataBase;
    }

    private boolean getIfTokenIsExpired(Jwt jwt){
        return Objects.requireNonNull(jwt.getExpiresAt()).isBefore(Instant.now());
    }
    private final UserRepo useruserInfoRepo;
    public UserDetails userDetails(String emailId){
        return useruserInfoRepo
                .findByEmail(emailId)
                .map(UserConfig::new)
                .orElseThrow(()-> new UsernameNotFoundException("UserEmail: "+emailId+" does not exist"));
    }

}
