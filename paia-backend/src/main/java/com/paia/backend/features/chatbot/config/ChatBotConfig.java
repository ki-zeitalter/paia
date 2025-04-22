package com.paia.backend.features.chatbot.config;

import com.paia.backend.features.chatbot.service.ChatBotAIService;
import com.paia.backend.features.chatbot.service.SystemMessageProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ChatBotConfig {

    private final SystemMessageProvider systemMessageProvider;

    @Bean
    ChatBotAIService chatBotAIService(StreamingChatLanguageModel streamingChatLanguageModel, ToolProvider toolProvider) {
        return AiServices
                .builder(ChatBotAIService.class)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .toolProvider(toolProvider)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(30))
                .systemMessageProvider(systemMessageProvider::provide)
                .build();
    }


}
