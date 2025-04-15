package com.paia.backend.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * Konvertiert Google OAuth2 Access Token Claims in ein Format, das mit Spring Security kompatibel ist.
 * Hilft bei der Lösung von Problemen mit unterschiedlichen Claim-Formaten in Access Tokens vs. ID Tokens.
 */
@Component
public class GoogleAccessTokenConverter implements Converter<Map<String, Object>, Map<String, Object>> {

    private final MappedJwtClaimSetConverter delegate = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());

    @Override
    public Map<String, Object> convert(Map<String, Object> claims) {
        // Zuerst die Standardkonvertierung durchführen
        Map<String, Object> convertedClaims = delegate.convert(claims);
        
        // Debug-Ausgaben
        System.out.println("Google Access Token Claims vor Konvertierung: " + claims);
        System.out.println("Google Access Token Claims nach Standardkonvertierung: " + convertedClaims);
        
        // In Google Access Tokens fehlt oft der "iss" Claim oder hat ein anderes Format
        if (!convertedClaims.containsKey("iss") && claims.containsKey("azp")) {
            convertedClaims.put("iss", "https://accounts.google.com");
            System.out.println("Issuer Claim hinzugefügt: https://accounts.google.com");
        }
        
        // Manchmal fehlt die Audience in Access Tokens
        if (!convertedClaims.containsKey("aud") && claims.containsKey("azp")) {
            convertedClaims.put("aud", Collections.singletonList(claims.get("azp")));
            System.out.println("Audience Claim aus 'azp' hinzugefügt: " + claims.get("azp"));
        }
        
        System.out.println("Finale konvertierte Claims: " + convertedClaims);
        
        return convertedClaims;
    }
} 