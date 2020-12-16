package com.github.sevtech.cloud.storage.spring.property

import org.springframework.core.env.Environment

class DropboxProperties(private val env: Environment) {
    private val ACCESS_TOKEN = "dropbox.accessToken"
    private val CLIENT_IDENTIFIER = "dropbox.clientIdentifier"

    val dropboxAccessToken: String?
        get() = env.getProperty(ACCESS_TOKEN)

    val clientIdentifier: String?
        get() = env.getProperty(CLIENT_IDENTIFIER)
}