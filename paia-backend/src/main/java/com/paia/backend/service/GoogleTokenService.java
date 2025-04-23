package com.paia.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleTokenService {

    private final GoogleIdTokenVerifier verifier;
    private final String clientId;

    public GoogleTokenService(@Value("${google.oauth2.client-id}") String clientId) {
        this.clientId = clientId;
        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        
        this.verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .setIssuer("https://accounts.google.com")
                .build();
                
        System.out.println("GoogleTokenService initialisiert mit clientId: " + clientId);
    }

    /**
     * Validiert ein Google ID-Token und extrahiert die Claims.
     * 
     * @param idTokenString Das zu validierende Google ID-Token
     * @return Eine Map mit den extrahierten Claims oder null, wenn das Token ungültig ist
     * @throws IOException Bei Fehlern bei der HTTP-Kommunikation
     * @throws GeneralSecurityException Bei Fehlern bei der Signaturprüfung
     */
    public Map<String, Object> validateAndExtractClaims(String idTokenString) 
            throws GeneralSecurityException, IOException {
        
        try {
            System.out.println("Validiere Google ID-Token: " + idTokenString.substring(0, Math.min(20, idTokenString.length())) + "...");
            
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                System.err.println("Ungültiges Google ID-Token");
                return null;
            }
            
            Payload payload = idToken.getPayload();
            
            // Claims extrahieren
            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", payload.getSubject());
            claims.put("email", payload.getEmail());
            claims.put("email_verified", payload.getEmailVerified());
            claims.put("name", payload.get("name"));
            claims.put("picture", payload.get("picture"));
            claims.put("given_name", payload.get("given_name"));
            claims.put("family_name", payload.get("family_name"));
            claims.put("locale", payload.get("locale"));
            
            System.out.println("Google ID-Token erfolgreich validiert für: " + payload.getEmail());
            return claims;
        } catch (IllegalArgumentException e) {
            System.err.println("Fehler beim Validieren des Google ID-Tokens: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
} 