package com.github.sevtech.cloud.storage.spring.config

import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import com.github.sevtech.cloud.storage.spring.property.DropboxProperties
import com.github.sevtech.cloud.storage.spring.service.StorageService
import com.github.sevtech.cloud.storage.spring.service.impl.DropboxService
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
@ConditionalOnCloudStorageProperty(value = "dropbox.enabled")
class DropboxConfig() {

    private val log = KotlinLogging.logger {}

    @Bean
    fun dropboxProperties(env: Environment): DropboxProperties {
        return DropboxProperties(env)
    }

    @Bean
    fun dbxClientV2(dropboxProperties: DropboxProperties): DbxClientV2? {
        log.info("Registering DbxClientV2")
        return DbxClientV2(DbxRequestConfig.newBuilder(
                dropboxProperties.clientIdentifier).build(), dropboxProperties.dropboxAccessToken)
    }

    @Bean
    fun dropboxService(dbxClientV2: DbxClientV2): StorageService {
        return DropboxService(dbxClientV2)
    }
}