package com.paia.backend.controller;

import com.paia.backend.dto.LlmModelDto;
import com.paia.backend.dto.ModelWithConfigDto;
import com.paia.backend.dto.UserModelConfigDto;
import com.paia.backend.service.LlmModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/models")
public class ModelConfigController {

    private final LlmModelService llmModelService;

    @Autowired
    public ModelConfigController(LlmModelService llmModelService) {
        this.llmModelService = llmModelService;
    }

    // Endpunkt für Admins zum Verwalten von Modellen
    @GetMapping("/admin")
    public ResponseEntity<List<LlmModelDto>> getAllModelsAdmin() {
        // Hier sollte eine Admin-Berechtigung geprüft werden
        List<LlmModelDto> models = llmModelService.getAllLlmModels();
        return ResponseEntity.ok(models);
    }

    @PostMapping("/admin")
    public ResponseEntity<LlmModelDto> createModel(@RequestBody LlmModelDto modelDto) {
        // Hier sollte eine Admin-Berechtigung geprüft werden
        LlmModelDto createdModel = llmModelService.createOrUpdateLlmModel(modelDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdModel);
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<LlmModelDto> updateModel(@PathVariable String id, @RequestBody LlmModelDto modelDto) {
        // Hier sollte eine Admin-Berechtigung geprüft werden
        modelDto.setId(id);
        LlmModelDto updatedModel = llmModelService.createOrUpdateLlmModel(modelDto);
        return ResponseEntity.ok(updatedModel);
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable String id) {
        // Hier sollte eine Admin-Berechtigung geprüft werden
        llmModelService.deleteLlmModel(id);
        return ResponseEntity.noContent().build();
    }

    // Endpunkte für Benutzer
    @GetMapping
    public ResponseEntity<List<ModelWithConfigDto>> getAllModelsForUser(Authentication authentication) {
        // Temporäre Lösung: Festen Benutzer-ID verwenden für Testzwecke
        String userId = "test-user"; 
        // Falls Authentication vorhanden ist, diese verwenden
        if (authentication != null) {
            userId = authentication.getName();
        }
        List<ModelWithConfigDto> models = llmModelService.getAllModelsWithConfigStatus(userId);
        return ResponseEntity.ok(models);
    }

    @GetMapping("/{id}/config")
    public ResponseEntity<UserModelConfigDto> getUserModelConfig(
            @PathVariable String id, Authentication authentication) {
        String userId = authentication.getName();
        return llmModelService.getUserModelConfig(userId, id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/config")
    public ResponseEntity<Void> saveUserModelConfig(
            @PathVariable String id,
            @RequestBody UserModelConfigDto configDto,
            Authentication authentication) {
        String userId = authentication.getName();
        llmModelService.saveUserModelConfig(userId, id, configDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/config")
    public ResponseEntity<Void> deleteUserModelConfig(
            @PathVariable String id, Authentication authentication) {
        String userId = authentication.getName();
        llmModelService.deleteUserModelConfig(userId, id);
        return ResponseEntity.noContent().build();
    }
} 