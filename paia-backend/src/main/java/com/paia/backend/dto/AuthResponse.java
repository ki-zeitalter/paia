package com.paia.backend.dto;

import com.paia.backend.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String id;
    private String email;
    private String name;
    private Set<UserRole> roles;
} 