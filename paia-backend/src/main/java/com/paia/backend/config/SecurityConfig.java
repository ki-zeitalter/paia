package com.paia.backend.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Import für HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Optional
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
// @EnableMethodSecurity // Optional: Aktivieren, wenn Methodensicherheit (@PreAuthorize etc.) genutzt wird
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
           .cors(cors -> cors.configurationSource(corsConfigurationSource))
           .authorizeHttpRequests(authorize -> authorize
                // Beispiel: Öffentliche Endpunkte erlauben
               //.requestMatchers("/api/public/**").permitAll()
                // Beispiel: Endpunkt erfordert bestimmten Scope/Authority
               //.requestMatchers(HttpMethod.GET, "/api/user/**").hasAuthority("SCOPE_profile")
                // Alle anderen Anfragen müssen authentifiziert sein
               .anyRequest().authenticated()
            )
            // Session Management auf STATELESS setzen (essenziell für Resource Server)
           .sessionManagement(session -> session
               .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // CSRF-Schutz deaktivieren (üblich für stateless APIs)
           .csrf(csrf -> csrf.disable())
            // OAuth2 Resource Server Konfiguration für JWTs aktivieren
           .oauth2ResourceServer(oauth2 -> oauth2
                // Standard-JWT-Verarbeitung aktivieren (nutzt auto-konfigurierten JwtDecoder)
               .jwt(withDefaults())
                // Alternative: Explizite Konfiguration, z.B. mit eigenem Converter
                //.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
            // CORS-Konfiguration aktivieren (ggf. separate Bean für Details)
            //.cors(withDefaults());

        return http.build();
    }

    // Optional: Bean für benutzerdefiniertes Claim-Mapping
    // @Bean
    // public JwtAuthenticationConverter jwtAuthenticationConverter() {
    //     //... Implementierung...
    // }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 