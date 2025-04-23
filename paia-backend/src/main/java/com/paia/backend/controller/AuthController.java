package com.paia.backend.controller;

import com.paia.backend.dto.AuthResponse;
import com.paia.backend.dto.GoogleTokenRequest;
import com.paia.backend.dto.LoginRequest;
import com.paia.backend.dto.RegisterRequest;
import com.paia.backend.model.User;
import com.paia.backend.service.JwtService;
import com.paia.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            if (request.getEmail() == null || request.getPassword() == null || request.getName() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder().token("ERROR: Fehlende Pflichtfelder").build());
            }
            
            System.out.println("Registrierungsanfrage erhalten für: " + request.getEmail());
            User user = userService.registerUser(request.getEmail(), request.getPassword(), request.getName());
            String token = jwtService.generateToken(user);
            
            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .roles(user.getRoles())
                    .build();
            
            System.out.println("Registrierung erfolgreich für: " + request.getEmail());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("Fehler bei Registrierung: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AuthResponse.builder().token("ERROR: " + e.getMessage()).build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            if (request.getEmail() == null || request.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder().token("ERROR: Fehlende Pflichtfelder").build());
            }
            
            System.out.println("Login-Anfrage erhalten für: " + request.getEmail());
            
            return userService.findByEmail(request.getEmail())
                    .filter(user -> userService.validatePassword(user, request.getPassword()))
                    .map(user -> {
                        User updatedUser = userService.updateLastLogin(user);
                        String token = jwtService.generateToken(updatedUser);
                        
                        AuthResponse response = AuthResponse.builder()
                                .token(token)
                                .id(updatedUser.getId())
                                .email(updatedUser.getEmail())
                                .name(updatedUser.getName())
                                .roles(updatedUser.getRoles())
                                .build();
                        
                        System.out.println("Login erfolgreich für: " + request.getEmail());
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        System.err.println("Login fehlgeschlagen für: " + request.getEmail() + " - Ungültige Anmeldedaten");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(AuthResponse.builder().token("ERROR: Ungültige Anmeldedaten").build());
                    });
        } catch (Exception e) {
            System.err.println("Fehler bei Login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AuthResponse.builder().token("ERROR: " + e.getMessage()).build());
        }
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody GoogleTokenRequest tokenRequest) {
        try {
            if (tokenRequest == null || tokenRequest.getIdToken() == null || tokenRequest.getIdToken().trim().isEmpty()) {
                System.err.println("Fehlerhaftes Token in Google-Login-Anfrage");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder().token("ERROR: Ungültiges Token").build());
            }
            
            System.out.println("Google-Login-Anfrage erhalten, Token: " + tokenRequest.getIdToken().substring(0, Math.min(20, tokenRequest.getIdToken().length())) + "...");
            
            // In einer realen Implementierung würdest du das Token validieren
            // und die Benutzerinformationen aus dem Token extrahieren
            // Dies ist vereinfacht und sollte durch eine echte Google-Token-Validierung ersetzt werden
            Map<String, Object> claims = extractClaimsFromGoogleToken(tokenRequest.getIdToken());
            
            String googleId = (String) claims.get("sub");
            String email = (String) claims.get("email");
            String name = (String) claims.get("name");
            
            if (email == null || email.trim().isEmpty()) {
                System.err.println("Keine E-Mail im Token gefunden");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder().token("ERROR: Keine E-Mail im Token gefunden").build());
            }
            
            User user = userService.createOrUpdateGoogleUser(googleId, email, name);
            String token = jwtService.generateToken(user);
            
            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .roles(user.getRoles())
                    .build();
            
            System.out.println("Google-Login erfolgreich für: " + email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Fehler bei Google-Login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthResponse.builder().token("ERROR: " + e.getMessage()).build());
        }
    }
    
    // Diese Methode ist ein Platzhalter und sollte in der realen Implementierung
    // durch eine echte Google-Token-Validierung ersetzt werden
    private Map<String, Object> extractClaimsFromGoogleToken(String idToken) {
        try {
            // Für eine einfache Implementation extrahieren wir die Claims direkt aus dem Token
            // ohne Signaturprüfung. In einer Produktionsumgebung sollte die Signatur überprüft werden.
            String[] chunks = idToken.split("\\.");
            if (chunks.length != 3) {
                throw new RuntimeException("Ungültiges Token-Format");
            }
            
            // Decodiere den Payload (zweiter Teil des JWT)
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));
            System.out.println("Google Token Payload: " + payload);
            
            // In einer realen Implementierung würde man ein JSON-Parsing-Framework verwenden
            // Hier verwenden wir eine hartcodierte Demo-Lösung für Testzwecke
            Map<String, Object> claims = new HashMap<>();
            String testEmail = "demo.user@example.com";
            
            // Werte aus dem Request-Parameter verwenden, wenn möglich
            if (idToken.contains("@")) {
                testEmail = idToken;
            }
            
            claims.put("sub", "google-id-" + System.currentTimeMillis());
            claims.put("email", testEmail); // Eine gültige Email-Adresse angeben
            claims.put("name", "Demo User");
            
            System.out.println("Extrahierte Claims: " + claims);
            return claims;
        } catch (Exception e) {
            System.err.println("Fehler beim Extrahieren der Claims aus dem Google-Token: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Extrahieren der Claims aus dem Google-Token", e);
        }
    }
} 