package com.paia.backend.features.chatbot.config;

import com.paia.backend.features.chatbot.service.ChatBotAIService;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatBotConfig {

    @Bean
    ChatBotAIService chatBotAIService(StreamingChatLanguageModel streamingChatLanguageModel, ToolProvider toolProvider) {
        return AiServices
                .builder(ChatBotAIService.class)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .toolProvider(toolProvider)
                //.chatMemory(chatMemory)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(30))
                .build();
    }


}
