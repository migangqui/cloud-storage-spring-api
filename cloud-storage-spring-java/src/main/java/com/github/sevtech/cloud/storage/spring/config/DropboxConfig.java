package com.github.sevtech.cloud.storage.spring.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.github.sevtech.cloud.storage.spring.property.DropboxProperties;
import com.github.sevtech.cloud.storage.spring.service.StorageService;
import com.github.sevtech.cloud.storage.spring.service.impl.DropboxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
@ConditionalOnCloudStorageProperty(value = "dropbox.enabled")
public class DropboxConfig {

    @Bean
    public DropboxProperties dropboxProperties(Environment env) {
        return new DropboxProperties(env);
    }

    @Bean
    public DbxClientV2 dbxClientV2(DropboxProperties dropboxProperties) {
        return new DbxClientV2(DbxRequestConfig.newBuilder(
                dropboxProperties.getClientIdentifier()).build(), dropboxProperties.getDropboxAccessToken());
    }

    @Bean
    public StorageService dropboxService(final DbxClientV2 dbxClientV2) {
        return new DropboxService(dbxClientV2);
    }

}
