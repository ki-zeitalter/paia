package com.paia.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paia.backend.model.deepchat.DeepChatMessageContent;
import com.paia.backend.model.deepchat.DeepChatMessageRole;
import com.paia.backend.model.deepchat.DeepChatRequestBody;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class ChatBotController {
    @PostMapping("/chat-stream")
    public Flux<DeepChatMessageContent> chatStreamFlux(@RequestBody DeepChatRequestBody requestBody) {
        var currentPrompt = requestBody.getMessages()[0].getText();

        String response = "Diese Funktion ist noch nicht implementiert.";

        return Flux.create(sink -> {
            sink.next(DeepChatMessageContent.builder().role(DeepChatMessageRole.ai).text(response).build());
            sink.complete();
        });
    }
}
