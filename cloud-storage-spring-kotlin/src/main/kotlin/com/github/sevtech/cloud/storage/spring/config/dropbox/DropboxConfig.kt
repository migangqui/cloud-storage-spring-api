package com.github.sevtech.cloud.storage.spring.config.dropbox

import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import com.github.sevtech.cloud.storage.spring.config.ConditionalOnCloudStorageProperty
import com.github.sevtech.cloud.storage.spring.property.dropbox.DropboxProperties
import com.github.sevtech.cloud.storage.spring.service.StorageService
import com.github.sevtech.cloud.storage.spring.service.dropbox.DropboxService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnCloudStorageProperty(value = "dropbox.enabled")
class DropboxConfig() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Bean
    fun dropboxProperties(): DropboxProperties {
        return DropboxProperties()
    }

    @Bean
    fun dbxClientV2(dropboxProperties: DropboxProperties): DbxClientV2? {
        log.info("Registering DbxClientV2")
        return DbxClientV2(DbxRequestConfig.newBuilder(
                dropboxProperties.clientIdentifier).build(), dropboxProperties.accessToken)
    }

    @Bean
    fun dropboxService(dbxClientV2: DbxClientV2): StorageService {
        return DropboxService(dbxClientV2)
    }
}