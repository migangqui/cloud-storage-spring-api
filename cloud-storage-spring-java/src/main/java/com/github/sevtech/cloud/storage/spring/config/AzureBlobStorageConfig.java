package com.github.sevtech.cloud.storage.spring.config;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.github.sevtech.cloud.storage.spring.property.AzureBlobStorageProperties;
import com.github.sevtech.cloud.storage.spring.service.StorageService;
import com.github.sevtech.cloud.storage.spring.service.impl.AzureBlobStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
@ConditionalOnCloudStorageProperty(value = "azure.blob.storage.enabled")
public class AzureBlobStorageConfig {

    @Bean
    public AzureBlobStorageProperties azureBlobStorageProperties(final Environment env) {
        return new AzureBlobStorageProperties(env);
    }

    @Bean
    public BlobServiceClient blobServiceClient(final AzureBlobStorageProperties azureBlobStorageProperties) {
        log.info("Registering Azure Blob Storage client");
        return new BlobServiceClientBuilder().connectionString(azureBlobStorageProperties.getConnectionString()).buildClient();
    }

    @Bean
    public StorageService azureBlobStorageService(final BlobServiceClient blobServiceClient) {
        return new AzureBlobStorageService(blobServiceClient);
    }

}
