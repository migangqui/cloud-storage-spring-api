package com.github.sevtech.cloud.storage.spring.config.dropbox;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.github.sevtech.cloud.storage.spring.config.ConditionalOnCloudStorageProperty;
import com.github.sevtech.cloud.storage.spring.property.dropbox.DropboxProperties;
import com.github.sevtech.cloud.storage.spring.service.StorageService;
import com.github.sevtech.cloud.storage.spring.service.dropbox.DropboxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnCloudStorageProperty(value = "dropbox.enabled")
public class DropboxConfig {

    @Bean
    public DropboxProperties dropboxProperties() {
        return new DropboxProperties();
    }

    @Bean
    public DbxClientV2 dbxClientV2(final DropboxProperties dropboxProperties) {
        log.info("Registering DbxClientV2");
        return new DbxClientV2(DbxRequestConfig.newBuilder(
                dropboxProperties.getClientIdentifier()).build(), dropboxProperties.getAccessToken());
    }

    @Bean
    public StorageService dropboxService(final DbxClientV2 dbxClientV2) {
        return new DropboxService(dbxClientV2);
    }

}
