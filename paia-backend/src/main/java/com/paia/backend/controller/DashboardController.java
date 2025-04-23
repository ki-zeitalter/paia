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
        try {
            if (authentication == null) {
                throw new IllegalArgumentException("Authentication ist null");
            }
            
            Object principal = authentication.getPrincipal();
            
            if (principal instanceof Jwt) {
                Jwt jwt = (Jwt) principal;
                System.out.println("JWT aus Authentication: " + jwt.getSubject());
                return userService.getOrCreateUserFromJwt(jwt);
            } else if (principal instanceof String) {
                // Fallback, wenn Principal ein String ist (z.B. anonymousUser)
                System.out.println("Principal ist ein String: " + principal);
                return userService.findByEmail((String) principal)
                    .orElseThrow(() -> new IllegalArgumentException("Benutzer nicht gefunden: " + principal));
            } else if (principal instanceof org.springframework.security.core.userdetails.User) {
                // Fallback für UserDetails
                String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
                System.out.println("Principal ist UserDetails mit Username: " + username);
                return userService.findByEmail(username)
                    .orElseThrow(() -> new IllegalArgumentException("Benutzer nicht gefunden: " + username));
            } else {
                System.err.println("Unbekannter Principal-Typ: " + (principal != null ? principal.getClass().getName() : "null"));
                throw new IllegalArgumentException("Unbekannter Authentication-Typ");
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Laden des Benutzers aus der Authentifizierung: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Fehler bei der Authentifizierung", e);
        }
    }
} 