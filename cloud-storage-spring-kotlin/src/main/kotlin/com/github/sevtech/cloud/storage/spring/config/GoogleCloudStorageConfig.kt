package com.github.sevtech.cloud.storage.spring.config

import com.github.sevtech.cloud.storage.spring.config.GoogleCloudStorageConfig.GoogleCloudStorageCondition
import com.github.sevtech.cloud.storage.spring.property.GoogleCloudStorageProperties
import com.github.sevtech.cloud.storage.spring.service.StorageService
import com.github.sevtech.cloud.storage.spring.service.impl.GoogleCloudStorageService
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import mu.KotlinLogging
import org.springframework.context.annotation.*
import org.springframework.core.env.Environment
import org.springframework.core.type.AnnotatedTypeMetadata
import java.io.FileInputStream
import java.io.IOException

@Configuration
@Conditional(GoogleCloudStorageCondition::class)
class GoogleCloudStorageConfig {

    private val log = KotlinLogging.logger {}

    @Bean
    fun googleCloudStorageProperties(env: Environment): GoogleCloudStorageProperties? {
        return GoogleCloudStorageProperties(env)
    }

    @Bean
    @Throws(IOException::class)
    fun storageClient(googleCloudStorageProperties: GoogleCloudStorageProperties): Storage? {
        log.info("Registering Google Storage client")
        return StorageOptions.newBuilder()
            .setCredentials(ServiceAccountCredentials.fromStream(FileInputStream(googleCloudStorageProperties.keyfileLocation)))
            .build()
            .service
    }

    @Bean
    fun googleCloudStorageService(storageClient: Storage): StorageService {
        return GoogleCloudStorageService(storageClient)
    }

    internal class GoogleCloudStorageCondition : Condition {
        override fun matches(
            conditionContext: ConditionContext,
            annotatedTypeMetadata: AnnotatedTypeMetadata
        ): Boolean {
            return conditionContext.environment.getProperty("gcp.storage.enabled") != null
        }
    }
}