package com.paia.backend.features.chatbot.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import reactor.core.publisher.Flux;


public interface ChatBotAIService {

    @SystemMessage(fromResource = "chatbot-system-message.txt")
    Flux<String> chatStream(@UserMessage String userMessage, @MemoryId @V("userName") String userName, @V("name") String name);
}
