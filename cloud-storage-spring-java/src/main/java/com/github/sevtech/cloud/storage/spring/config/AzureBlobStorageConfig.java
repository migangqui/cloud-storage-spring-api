package com.github.sevtech.cloud.storage.spring.config;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.github.sevtech.cloud.storage.spring.property.AzureBlobStorageProperties;
import com.github.sevtech.cloud.storage.spring.property.GoogleCloudStorageProperties;
import com.github.sevtech.cloud.storage.spring.service.StorageService;
import com.github.sevtech.cloud.storage.spring.service.impl.AzureBlobStorageService;
import com.github.sevtech.cloud.storage.spring.service.impl.GoogleCloudStorageService;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Configuration
@Conditional(AzureBlobStorageConfig.AzureBlobStorageCondition.class)
public class AzureBlobStorageConfig {

    @Bean
    public AzureBlobStorageProperties azureBlobStorageProperties(Environment env) {
        return new AzureBlobStorageProperties(env);
    }

    @Bean
    public BlobContainerClient storageClient(AzureBlobStorageProperties azureBlobStorageProperties) throws IOException {
        log.info("Registering Azure Blob Storage client");

        return new BlobContainerClientBuilder()
                .connectionString(azureBlobStorageProperties.getConnectionString())
                .buildClient();
    }

    @Bean
    public StorageService azureBlobStorageService(BlobContainerClient client) {
        return new AzureBlobStorageService(client);
    }

    static class AzureBlobStorageCondition implements Condition {
        @Override
        public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
            return conditionContext.getEnvironment().getProperty("azure.blob.storage.enabled") != null;
        }
    }
}
