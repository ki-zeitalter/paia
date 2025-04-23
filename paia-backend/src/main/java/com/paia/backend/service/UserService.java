package com.paia.backend.service;

import com.paia.backend.model.User;
import com.paia.backend.model.UserRole;
import com.paia.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String email, String password, String name) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("E-Mail wird bereits verwendet");
        }

        User user = User.builder()
                .email(email)
                .name(name)
                .passwordHash(passwordEncoder.encode(password))
                .roles(Collections.singleton(UserRole.USER))
                .createdAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }

    public boolean validatePassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPasswordHash());
    }

    public User createOrUpdateGoogleUser(String googleId, String email, String name) {
        if (googleId == null || googleId.trim().isEmpty()) {
            throw new IllegalArgumentException("Google ID darf nicht null oder leer sein");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("E-Mail darf nicht null oder leer sein");
        }
        
        System.out.println("Google-User-Erstellung/Update mit: googleId=" + googleId + ", email=" + email + ", name=" + name);
        
        Optional<User> existingUser = userRepository.findByGoogleId(googleId);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            
            // Stelle sicher, dass Email gesetzt ist
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                user.setEmail(email);
            }
            
            user.setLastLoginAt(LocalDateTime.now());
            System.out.println("Bestehender Google-Benutzer aktualisiert: " + user.getId() + ", email=" + user.getEmail());
            return userRepository.save(user);
        }
        
        // Prüfen, ob eine E-Mail-Adresse bereits existiert
        Optional<User> emailUser = userRepository.findByEmail(email);
        if (emailUser.isPresent()) {
            // Verknüpfe Google-ID mit existierendem Benutzer
            User user = emailUser.get();
            user.setGoogleId(googleId);
            user.setLastLoginAt(LocalDateTime.now());
            
            // Stelle sicher, dass Name gesetzt ist
            if (name != null && !name.trim().isEmpty() && (user.getName() == null || user.getName().trim().isEmpty())) {
                user.setName(name);
            }
            
            System.out.println("Bestehender E-Mail-Benutzer mit Google verknüpft: " + user.getId());
            return userRepository.save(user);
        }
        
        // Name auf Standardwert setzen, falls null
        String userName = (name != null && !name.trim().isEmpty()) ? name : "User " + email.split("@")[0];
        
        // Neuen Benutzer erstellen
        User newUser = User.builder()
                .email(email)
                .name(userName)
                .googleId(googleId)
                .roles(Collections.singleton(UserRole.USER))
                .createdAt(LocalDateTime.now())
                .lastLoginAt(LocalDateTime.now())
                .build();
        
        System.out.println("Neuer Google-Benutzer erstellt mit Email: " + email);
        return userRepository.save(newUser);
    }

    public User updateLastLogin(User user) {
        user.setLastLoginAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Diese Methode wird für Backward-Kompatibilität mit vorhandenen Controllern beibehalten.
     * Sie extrahiert Benutzerinformationen aus dem JWT-Token und erstellt oder aktualisiert
     * einen Benutzer in der Datenbank.
     */
    public User getOrCreateUserFromJwt(Jwt jwt) {
        String subject = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");

        System.out.println("JWT Claims extrahiert: subject=" + subject + ", email=" + email + ", name=" + name);
        
        // Sicherstellen, dass wir eine gültige E-Mail haben
        if (email == null || email.trim().isEmpty()) {
            // Wenn keine E-Mail im Token, verwenden wir subject als E-Mail (falls es wie eine E-Mail aussieht)
            if (subject != null && subject.contains("@")) {
                email = subject;
                System.out.println("Keine E-Mail im Token, verwende Subject als E-Mail: " + email);
            }
            
            // Wenn immer noch keine E-Mail, nehmen wir den Name-Claim, falls er wie eine E-Mail aussieht
            else if (name != null && name.contains("@")) {
                email = name;
                System.out.println("Keine E-Mail im Subject, verwende Name als E-Mail: " + email);
            }
            
            // Wenn wir immer noch keine E-Mail haben, verwenden wir eine Dummy-E-Mail mit dem Subject
            else {
                email = "user-" + (subject != null ? subject : System.currentTimeMillis()) + "@paia-system.local";
                System.out.println("Generiere Dummy-E-Mail: " + email);
            }
        }
        
        // Name sicherstellen, falls null
        if (name == null || name.trim().isEmpty()) {
            name = email.split("@")[0]; // Verwende Benutzernamen aus der E-Mail
            System.out.println("Name ist leer, verwende Usernamen aus E-Mail: " + name);
        }

        // Zuerst nach Google-ID suchen
        Optional<User> existingUserByGoogleId = userRepository.findByGoogleId(subject);
        if (existingUserByGoogleId.isPresent()) {
            User user = existingUserByGoogleId.get();
            
            // Stelle sicher, dass die E-Mail nicht null ist
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                user.setEmail(email);
                System.out.println("E-Mail für bestehenden Google-Benutzer aktualisiert: " + email);
            }
            
            user.setLastLoginAt(LocalDateTime.now());
            return userRepository.save(user);
        }

        // Dann nach E-Mail suchen
        Optional<User> existingUserByEmail = userRepository.findByEmail(email);
        if (existingUserByEmail.isPresent()) {
            User user = existingUserByEmail.get();
            
            // Nur Google-ID setzen, wenn sie nicht null ist
            if (subject != null && !subject.trim().isEmpty()) {
                user.setGoogleId(subject);
            }
            
            user.setLastLoginAt(LocalDateTime.now());
            System.out.println("Bestehender E-Mail-Benutzer gefunden und aktualisiert: " + email);
            return userRepository.save(user);
        }

        // Neuen Benutzer erstellen
        User newUser = User.builder()
                .email(email) // Jetzt ist sichergestellt, dass E-Mail nicht null ist
                .name(name)
                .googleId(subject)
                .roles(Collections.singleton(UserRole.USER))
                .createdAt(LocalDateTime.now())
                .lastLoginAt(LocalDateTime.now())
                .build();

        System.out.println("Neuer Benutzer aus JWT erstellt: email=" + email + ", name=" + name);
        return userRepository.save(newUser);
    }
} 