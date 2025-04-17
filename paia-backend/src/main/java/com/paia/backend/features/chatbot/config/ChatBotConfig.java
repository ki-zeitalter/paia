package com.paia.backend.features.chatbot.config;

import com.paia.backend.features.chatbot.service.ChatBotAIService;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatBotConfig {

    @Bean
    ChatBotAIService chatBotAIService(StreamingChatLanguageModel streamingChatLanguageModel) {
        return AiServices
                .builder(ChatBotAIService.class)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .build();
    }

}
