package com.github.sevtech.cloud.storage.spring.config.azure

import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.BlobServiceClientBuilder
import com.github.sevtech.cloud.storage.spring.config.ConditionalOnCloudStorageProperty
import com.github.sevtech.cloud.storage.spring.property.azure.AzureBlobStorageProperties
import com.github.sevtech.cloud.storage.spring.service.StorageService
import com.github.sevtech.cloud.storage.spring.service.azure.AzureBlobStorageService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnCloudStorageProperty(value = "azure.blob.storage.enabled")
class AzureBlobStorageConfig {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Bean
    fun azureBlobStorageProperties(): AzureBlobStorageProperties {
        return AzureBlobStorageProperties()
    }

    @Bean
    fun blobServiceClient(azureBlobStorageProperties: AzureBlobStorageProperties): BlobServiceClient {
        log.info("Registering Azure Blob Storage client")
        return BlobServiceClientBuilder().connectionString(azureBlobStorageProperties.connectionString).buildClient()
    }

    @Bean
    fun azureBlobStorageService(blobServiceClient: BlobServiceClient): StorageService {
        return AzureBlobStorageService(blobServiceClient)
    }
}