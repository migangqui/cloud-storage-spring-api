package com.github.sevtech.cloud.storage.spring.config

import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.BlobServiceClientBuilder
import com.github.sevtech.cloud.storage.spring.property.AzureBlobStorageProperties
import com.github.sevtech.cloud.storage.spring.service.StorageService
import com.github.sevtech.cloud.storage.spring.service.impl.AzureBlobStorageService
import mu.KotlinLogging
import org.springframework.context.annotation.*
import org.springframework.core.env.Environment
import org.springframework.core.type.AnnotatedTypeMetadata

@Configuration
@Conditional(AzureBlobStorageConfig.AzureBlobStorageCondition::class)
class AzureBlobStorageConfig {

    private val log = KotlinLogging.logger {}

    @Bean
    fun azureBlobStorageProperties(env: Environment): AzureBlobStorageProperties {
        return AzureBlobStorageProperties(env)
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

    internal class AzureBlobStorageCondition : Condition {
        override fun matches(conditionContext: ConditionContext, annotatedTypeMetadata: AnnotatedTypeMetadata): Boolean {
            val condition = conditionContext.environment.getProperty("azure.blob.storage.enabled", Boolean::class.java)
            return condition != null && condition
        }
    }
}