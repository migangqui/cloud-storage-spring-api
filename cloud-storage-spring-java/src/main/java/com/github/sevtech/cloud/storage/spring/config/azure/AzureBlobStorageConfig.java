package com.github.sevtech.cloud.storage.spring.config.azure;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.github.sevtech.cloud.storage.spring.config.ConditionalOnCloudStorageProperty;
import com.github.sevtech.cloud.storage.spring.property.azure.AzureBlobStorageProperties;
import com.github.sevtech.cloud.storage.spring.service.StorageService;
import com.github.sevtech.cloud.storage.spring.service.azure.AzureBlobStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnCloudStorageProperty(value = "azure.blob.storage.enabled")
public class AzureBlobStorageConfig {

    @Bean
    public AzureBlobStorageProperties azureBlobStorageProperties() {
        return new AzureBlobStorageProperties();
    }

    @Bean
    public BlobServiceClient blobServiceClient(final AzureBlobStorageProperties azureBlobStorageProperties) {
        log.info("Registering Azure Blob Storage client");
        return new BlobServiceClientBuilder().connectionString(
                azureBlobStorageProperties.getConnectionString()).buildClient();
    }

    @Bean
    public StorageService azureBlobStorageService(final BlobServiceClient blobServiceClient) {
        return new AzureBlobStorageService(blobServiceClient);
    }

}
