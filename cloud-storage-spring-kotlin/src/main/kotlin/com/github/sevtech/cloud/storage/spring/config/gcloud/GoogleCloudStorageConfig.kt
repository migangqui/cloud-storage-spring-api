package com.github.sevtech.cloud.storage.spring.config.gcloud

import com.github.sevtech.cloud.storage.spring.config.ConditionalOnCloudStorageProperty
import com.github.sevtech.cloud.storage.spring.property.gcloud.GoogleCloudStorageProperties
import com.github.sevtech.cloud.storage.spring.service.StorageService
import com.github.sevtech.cloud.storage.spring.service.gcloud.GoogleCloudStorageService
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream
import java.io.IOException

@Configuration
@ConditionalOnCloudStorageProperty(value = "gcp.storage.enabled")
class GoogleCloudStorageConfig {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Bean
    fun googleCloudStorageProperties(): GoogleCloudStorageProperties? {
        return GoogleCloudStorageProperties()
    }

    @Bean
    @Throws(IOException::class)
    fun storageClient(googleCloudStorageProperties: GoogleCloudStorageProperties): Storage? {
        log.info("Registering Google Storage client")
        return StorageOptions.newBuilder()
            .setCredentials(ServiceAccountCredentials.fromStream(FileInputStream(googleCloudStorageProperties.keyfile!!)))
            .build()
            .service
    }

    @Bean
    fun googleCloudStorageService(storageClient: Storage): StorageService {
        return GoogleCloudStorageService(storageClient)
    }

}