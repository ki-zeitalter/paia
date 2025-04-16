package com.paia.backend.service;

import com.paia.backend.dto.LlmModelDto;
import com.paia.backend.dto.ModelWithConfigDto;
import com.paia.backend.dto.UserModelConfigDto;
import com.paia.backend.model.LlmModel;
import com.paia.backend.model.UserModelConfig;
import com.paia.backend.repository.LlmModelRepository;
import com.paia.backend.repository.UserModelConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class LlmModelService {

    private final LlmModelRepository llmModelRepository;
    private final UserModelConfigRepository userModelConfigRepository;
    private final TextEncryptor textEncryptor;

    @Autowired
    public LlmModelService(
            LlmModelRepository llmModelRepository,
            UserModelConfigRepository userModelConfigRepository,
            TextEncryptor textEncryptor) {
        this.llmModelRepository = llmModelRepository;
        this.userModelConfigRepository = userModelConfigRepository;
        this.textEncryptor = textEncryptor;
    }

    // Erstellt ein neues LLM-Modell oder aktualisiert ein bestehendes
    public LlmModelDto createOrUpdateLlmModel(LlmModelDto modelDto) {
        LlmModel model = llmModelRepository.findById(modelDto.getId())
                .orElse(LlmModel.builder()
                        .id(modelDto.getId() != null ? modelDto.getId() : UUID.randomUUID().toString())
                        .createdAt(LocalDateTime.now())
                        .build());

        model.setName(modelDto.getName());
        model.setIconUrl(modelDto.getIconUrl());
        model.setUpdatedAt(LocalDateTime.now());

        model = llmModelRepository.save(model);
        return mapToDto(model);
    }

    // Gibt alle verfügbaren LLM-Modelle zurück
    public List<LlmModelDto> getAllLlmModels() {
        return llmModelRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Gibt ein bestimmtes LLM-Modell zurück
    public Optional<LlmModelDto> getLlmModelById(String id) {
        return llmModelRepository.findById(id)
                .map(this::mapToDto);
    }

    // Löscht ein LLM-Modell
    public void deleteLlmModel(String id) {
        llmModelRepository.deleteById(id);
    }

    // Speichert die Konfiguration eines Nutzers für ein Modell
    public void saveUserModelConfig(String userId, String modelId, UserModelConfigDto configDto) {
        UserModelConfig config = userModelConfigRepository.findByUserIdAndModelId(userId, modelId)
                .orElse(UserModelConfig.builder()
                        .userId(userId)
                        .modelId(modelId)
                        .createdAt(LocalDateTime.now())
                        .build());

        // API-Key verschlüsseln
        if (configDto.getApiKey() != null && !configDto.getApiKey().isEmpty()) {
            config.setApiKey(textEncryptor.encrypt(configDto.getApiKey()));
        }

        config.setUpdatedAt(LocalDateTime.now());
        userModelConfigRepository.save(config);
    }

    // Gibt die Konfiguration eines Nutzers für ein Modell zurück
    public Optional<UserModelConfigDto> getUserModelConfig(String userId, String modelId) {
        return userModelConfigRepository.findByUserIdAndModelId(userId, modelId)
                .map(config -> {
                    UserModelConfigDto dto = new UserModelConfigDto();
                    dto.setModelId(config.getModelId());
                    if (config.getApiKey() != null) {
                        // API-Key nicht zurückgeben, nur "configured" flag
                        dto.setApiKey("*****");
                    }
                    return dto;
                });
    }

    // Gibt alle Modelle mit Konfigurationsstatus für einen Nutzer zurück
    public List<ModelWithConfigDto> getAllModelsWithConfigStatus(String userId) {
        List<LlmModel> allModels = llmModelRepository.findAll();
        List<UserModelConfig> userConfigs = userModelConfigRepository.findByUserId(userId);

        return allModels.stream()
                .map(model -> {
                    boolean configured = userConfigs.stream()
                            .anyMatch(c -> c.getModelId().equals(model.getId()) && c.getApiKey() != null);

                    return ModelWithConfigDto.builder()
                            .id(model.getId())
                            .name(model.getName())
                            .iconUrl(model.getIconUrl())
                            .configured(configured)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // Entfernt die Konfiguration eines Nutzers für ein Modell
    public void deleteUserModelConfig(String userId, String modelId) {
        userModelConfigRepository.findByUserIdAndModelId(userId, modelId)
                .ifPresent(userModelConfigRepository::delete);
    }

    // Hilfsmethode zur Konvertierung von Entity zu DTO
    private LlmModelDto mapToDto(LlmModel model) {
        return LlmModelDto.builder()
                .id(model.getId())
                .name(model.getName())
                .iconUrl(model.getIconUrl())
                .build();
    }
} 