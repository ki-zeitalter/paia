package com.paia.backend.controller;

import com.paia.backend.dto.DashboardConfigurationDto;
import com.paia.backend.model.User;
import com.paia.backend.service.DashboardService;
import com.paia.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardConfigurationDto> getDashboard(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        return ResponseEntity.ok(dashboardService.getDashboardConfiguration(user));
    }

    @PutMapping("/dashboard")
    public ResponseEntity<Void> saveDashboard(@RequestBody DashboardConfigurationDto configDto, 
                                             Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        dashboardService.saveDashboardConfiguration(user, configDto);
        return ResponseEntity.ok().build();
    }

    private User getUserFromAuthentication(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return userService.getOrCreateUserFromJwt(jwt);
    }
} 