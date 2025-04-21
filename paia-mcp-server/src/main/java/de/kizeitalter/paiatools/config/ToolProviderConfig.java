package de.kizeitalter.paiatools.config;

import de.kizeitalter.paiatools.services.ToDoService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolProviderConfig {
    @Bean
    public ToolCallbackProvider todoTools(ToDoService todoService) {
        return MethodToolCallbackProvider.builder().toolObjects(todoService).build();
    }
}
