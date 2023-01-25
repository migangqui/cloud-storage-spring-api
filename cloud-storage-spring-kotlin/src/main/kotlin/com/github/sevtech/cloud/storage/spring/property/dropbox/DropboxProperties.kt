package com.github.sevtech.cloud.storage.spring.property.dropbox

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "dropbox")
class DropboxProperties {
    var accessToken: String? = null
    var clientIdentifier: String? = null
}