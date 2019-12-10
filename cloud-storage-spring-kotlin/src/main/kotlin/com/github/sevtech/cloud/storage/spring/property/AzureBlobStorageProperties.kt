package com.github.sevtech.cloud.storage.spring.property

import org.springframework.core.env.Environment

class AzureBlobStorageProperties(private val env: Environment) {
    private val CONNECTION_STRING = "azure.blob.storage.connectionString"
    private val CONTAINER_NAME = "azure.blob.storage.container.name"

    val connectionString: String?
        get() = env.getProperty(CONNECTION_STRING)

    val containerName: String?
        get() = env.getProperty(CONTAINER_NAME)
}