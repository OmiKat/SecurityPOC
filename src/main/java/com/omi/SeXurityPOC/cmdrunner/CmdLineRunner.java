package com.omi.SeXurityPOC.cmdrunner;

import com.omi.SeXurityPOC.pojos.Roles;
import com.omi.SeXurityPOC.pojos.User;
import com.omi.SeXurityPOC.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CmdLineRunner implements CommandLineRunner {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        User admin = new User();
        admin.setUserName("admin");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setEmail("admin@admin.com");
        admin.setRoles("ADMIN");
        admin.setMobileNumber("123");

        User manager = new User();
        manager.setUserName("manager");
        manager.setPassword(passwordEncoder.encode("password"));
        manager.setEmail("manager@manager.com");
        manager.setRoles("MANAGER");
        manager.setMobileNumber("123");

        User user = new User();
        user.setUserName("user");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail("user@user.com");
        user.setRoles("USER");
        user.setMobileNumber("123");

        userRepo.saveAll(List.of(user,admin,manager));
    }
}
