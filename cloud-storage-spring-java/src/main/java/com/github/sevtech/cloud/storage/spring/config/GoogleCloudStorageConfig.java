package com.github.sevtech.cloud.storage.spring.config;

import com.github.sevtech.cloud.storage.spring.property.GoogleCloudStorageProperties;
import com.github.sevtech.cloud.storage.spring.service.StorageService;
import com.github.sevtech.cloud.storage.spring.service.impl.GoogleCloudStorageService;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Configuration
@ConditionalOnCloudStorageProperty(value = "gcp.storage.enabled")
public class GoogleCloudStorageConfig {

    @Bean
    public GoogleCloudStorageProperties googleCloudStorageProperties() {
        return new GoogleCloudStorageProperties();
    }

    @Bean
    public Storage storageClient(final GoogleCloudStorageProperties googleCloudStorageProperties) throws IOException {
        log.info("Registering Google Storage client");
        return StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(googleCloudStorageProperties.getKeyfile())))
                .build()
                .getService();
    }

    @Bean
    public StorageService googleCloudStorageService(final Storage storageClient) {
        return new GoogleCloudStorageService(storageClient);
    }

}
