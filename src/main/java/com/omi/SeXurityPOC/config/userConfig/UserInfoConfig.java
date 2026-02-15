package com.omi.SeXurityPOC.config.userConfig;

import com.omi.SeXurityPOC.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoConfig implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        return userRepo
                .findByEmail(emailId)
                .map(UserConfig::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + emailId));
    }
}
