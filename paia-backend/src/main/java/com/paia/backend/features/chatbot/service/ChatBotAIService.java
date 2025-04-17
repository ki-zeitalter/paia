package com.paia.backend.features.chatbot.service;

import dev.langchain4j.service.SystemMessage;
import reactor.core.publisher.Flux;


public interface ChatBotAIService {

    @SystemMessage(fromResource = "chatbot-system-message.txt")
    Flux<String> chatStream(String userMessage);
}
