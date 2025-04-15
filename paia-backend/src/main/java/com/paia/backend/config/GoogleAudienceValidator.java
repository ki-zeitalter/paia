package com.paia.backend.config;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class GoogleAudienceValidator implements OAuth2TokenValidator<Jwt> {
    
    private final String clientId;
    private final OAuth2Error error = new OAuth2Error(
            "invalid_token",
            "Die Audience ist ungültig",
            null
    );

    public GoogleAudienceValidator(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        String audience = jwt.getAudience().stream()
                .filter(aud -> aud.equals(clientId))
                .findFirst()
                .orElse(null);

        if (audience == null) {
            return OAuth2TokenValidatorResult.failure(error);
        }

        return OAuth2TokenValidatorResult.success();
    }
} 