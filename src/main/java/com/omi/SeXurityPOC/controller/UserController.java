package com.omi.SeXurityPOC.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class UserController {

//    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping("/welcome_msg")
    public ResponseEntity<String> welcomeMsg(Authentication authentication){
        return ResponseEntity.ok("ah aaja bsdk" + authentication);
    }

//    @PreAuthorize("hasRole('MANAGER')")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping("/msg-manager")
    public ResponseEntity<String> getManagerData(Principal principal){
         return ResponseEntity.ok("Ts  manager is shgit " + principal.getName());
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @GetMapping("/msg-admin")
    public ResponseEntity<String> getAdminData(Principal principal){
        return ResponseEntity.ok("this is Admin : " + principal.getName());
    }
}
