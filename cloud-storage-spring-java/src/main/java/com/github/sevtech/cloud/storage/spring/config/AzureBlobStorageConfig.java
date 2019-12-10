package com.github.sevtech.cloud.storage.spring.config;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.github.sevtech.cloud.storage.spring.property.AzureBlobStorageProperties;
import com.github.sevtech.cloud.storage.spring.service.StorageService;
import com.github.sevtech.cloud.storage.spring.service.impl.AzureBlobStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Slf4j
@Configuration
@Conditional(AzureBlobStorageConfig.AzureBlobStorageCondition.class)
public class AzureBlobStorageConfig {

    @Bean
    public AzureBlobStorageProperties azureBlobStorageProperties(Environment env) {
        return new AzureBlobStorageProperties(env);
    }

    @Bean
    public BlobServiceClient blobServiceClient(AzureBlobStorageProperties azureBlobStorageProperties) {
        log.info("Registering Azure Blob Storage client");

        return new BlobServiceClientBuilder().connectionString(azureBlobStorageProperties.getConnectionString()).buildClient();
    }

    @Bean
    public StorageService azureBlobStorageService(BlobServiceClient blobServiceClient) {
        return new AzureBlobStorageService(blobServiceClient);
    }

    static class AzureBlobStorageCondition implements Condition {
        @Override
        public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
            Boolean condition = conditionContext.getEnvironment().getProperty("azure.blob.storage.enabled", Boolean.class);
            return condition != null && condition;
        }
    }

}
