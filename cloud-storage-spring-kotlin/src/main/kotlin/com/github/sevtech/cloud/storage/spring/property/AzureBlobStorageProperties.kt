package com.github.sevtech.cloud.storage.spring.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "azure.blob.storage")
class AzureBlobStorageProperties {
    var connectionString: String? = null
    var containerName: String? = null
}