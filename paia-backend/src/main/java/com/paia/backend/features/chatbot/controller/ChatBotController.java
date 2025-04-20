package com.paia.backend.features.chatbot.controller;

import com.paia.backend.features.chatbot.service.ChatBotAIService;
import com.paia.backend.model.deepchat.DeepChatMessageContent;
import com.paia.backend.model.deepchat.DeepChatMessageRole;
import com.paia.backend.model.deepchat.DeepChatRequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class ChatBotController {

    private final ChatBotAIService assistant;

    @PostMapping("/chat-stream")
    public Flux<DeepChatMessageContent> chatStreamFlux(@RequestBody DeepChatRequestBody requestBody) {
        var currentPrompt = requestBody.getMessages()[0].getText();

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


        return this.assistant.chatStream(currentPrompt, userName, name)
                .map(message ->
                        DeepChatMessageContent.builder()
                                .text(message)
                                .role(DeepChatMessageRole.ai)
                                .build());
    }
}

