package com.paia.backend.service;

import com.paia.backend.model.User;
import com.paia.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User getOrCreateUserFromJwt(Jwt jwt) {
        String userId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");

        Optional<User> existingUser = userRepository.findById(userId);
        
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            User newUser = User.builder()
                    .id(userId)
                    .email(email)
                    .name(name)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            return userRepository.save(newUser);
        }
    }
} 