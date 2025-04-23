package com.paia.backend.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paia.backend.model.User;
import com.paia.backend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserInfoController {

    private final UserService userService;

    @GetMapping("/me")
    public Map<String, Object> getUserInfo(Authentication authentication) {
        Map<String, Object> userInfo = new HashMap<>();
        
        // Rollen aus der Authentifizierung extrahieren
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));
        
        // Prüfen, ob es sich um ein OAuth2-Token oder ein lokales Token handelt
        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            User user = userService.getOrCreateUserFromJwt(jwt);
            
            userInfo.put("id", user.getId());
            userInfo.put("email", user.getEmail());
            userInfo.put("name", user.getName());
            userInfo.put("authType", "oauth2");
            userInfo.put("roles", roles);
            userInfo.put("lastLogin", user.getLastLoginAt());
        } else {
            org.springframework.security.core.userdetails.User springUser = 
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            
            userInfo.put("email", springUser.getUsername());
            userInfo.put("authType", "local");
            userInfo.put("roles", roles);
        }
        
        return userInfo;
    }
} 