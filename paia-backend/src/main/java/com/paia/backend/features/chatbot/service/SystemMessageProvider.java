package com.paia.backend.features.chatbot.service;

import com.paia.backend.model.User;
import com.paia.backend.service.UserService;
import dev.langchain4j.model.input.PromptTemplate;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class SystemMessageProvider {

    private final String systemMessageText;
    private final UserService userService;

    @SneakyThrows
    @Autowired
    public SystemMessageProvider(@Value("classpath:chatbot-system-message.txt") Resource systemMessage,
                                UserService userService) {
        this.userService = userService;
        try (InputStream is = systemMessage.getInputStream()) {
            systemMessageText = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Provides the system message text with user information from the database.
     *
     * @param chatMemoryId The chat memory ID (not used in this implementation).
     * @return The system message text with user information and current date.
     */
    public String provide(Object chatMemoryId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        String name = userName;

        // Wenn es ein JWT-Token ist, holen wir die Benutzer aus der Datenbank
        var principal = authentication.getPrincipal();
        if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            User user = userService.getOrCreateUserFromJwt(jwt);
            
            // Verwenden der Daten aus der Datenbank
            userName = user.getEmail();
            name = user.getName();
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", userName);
        variables.put("name", name);
        variables.put("currentDate", java.time.LocalDateTime.now().toString());
        return PromptTemplate.from(systemMessageText).apply(variables).text();
    }

}
