package com.paia.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

@Configuration
public class EncryptionConfig {

    @Value("${paia.encryption.password}")
    private String encryptionPassword;


    @Bean
    public TextEncryptor textEncryptor() {
        return Encryptors.text(encryptionPassword, KeyGenerators.string().generateKey());
    }
} 