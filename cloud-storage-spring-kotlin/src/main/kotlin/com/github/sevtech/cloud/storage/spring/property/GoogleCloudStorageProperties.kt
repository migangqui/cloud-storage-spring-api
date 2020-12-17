package com.github.sevtech.cloud.storage.spring.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "gcp.storage")
class GoogleCloudStorageProperties {
    var keyfile: String? = null
}