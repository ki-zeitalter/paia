package com.paia.backend.service;

import com.paia.backend.dto.DashboardConfigurationDto;
import com.paia.backend.model.DashboardConfiguration;
import com.paia.backend.model.User;
import com.paia.backend.repository.DashboardConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardConfigurationRepository dashboardConfigurationRepository;

    @Transactional(readOnly = true)
    public DashboardConfigurationDto getDashboardConfiguration(User user) {
        Optional<DashboardConfiguration> dashboardConfigOpt = dashboardConfigurationRepository.findByUser(user);
        
        if (dashboardConfigOpt.isPresent()) {
            return mapToDto(dashboardConfigOpt.get());
        } else {
            // Rückgabe einer leeren Konfiguration, wenn keine existiert
            return new DashboardConfigurationDto("[]");
        }
    }

    @Transactional
    public void saveDashboardConfiguration(User user, DashboardConfigurationDto configDto) {
        Optional<DashboardConfiguration> existingConfig = dashboardConfigurationRepository.findByUser(user);
        
        if (existingConfig.isPresent()) {
            DashboardConfiguration config = existingConfig.get();
            config.setConfiguration(configDto.getConfiguration());
            config.setUpdatedAt(LocalDateTime.now());
            dashboardConfigurationRepository.save(config);
        } else {
            DashboardConfiguration newConfig = DashboardConfiguration.builder()
                    .user(user)
                    .configuration(configDto.getConfiguration())
                    .updatedAt(LocalDateTime.now())
                    .build();
            dashboardConfigurationRepository.save(newConfig);
        }
    }
    
    private DashboardConfigurationDto mapToDto(DashboardConfiguration config) {
        return DashboardConfigurationDto.builder()
                .configuration(config.getConfiguration())
                .build();
    }
} 