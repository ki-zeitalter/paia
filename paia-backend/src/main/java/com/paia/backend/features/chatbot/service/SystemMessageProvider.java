package com.paia.backend.features.chatbot.service;

import dev.langchain4j.model.input.PromptTemplate;
import lombok.SneakyThrows;
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

    @SneakyThrows
    public SystemMessageProvider(@Value("classpath:chatbot-system-message.txt") Resource systemMessage) {
        try (InputStream is = systemMessage.getInputStream()) {
            systemMessageText = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Provides the system message text with the current date.
     *
     * @param chatMemoryId The chat memory ID (not used in this implementation).
     * @return The system message text with the current date.
     */
    public String provide(Object chatMemoryId) {

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        String name = userName;

        var principal = authentication.getPrincipal();
        if (principal instanceof Jwt) {
            var jwt = (Jwt) principal;
            var email = jwt.getClaimAsString("email");
            name = jwt.getClaimAsString("name");

            userName = email;
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", userName);
        variables.put("name", name);
        variables.put("currentDate", java.time.LocalDateTime.now().toString());
        return PromptTemplate.from(systemMessageText).apply(variables).text();

    }

}
