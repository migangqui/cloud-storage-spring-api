package com.github.migangqui.cloud.storage.spring.property

import org.springframework.core.env.Environment

class GoogleCloudStorageProperties(private val env: Environment) {

    private val KEYFILE_LOCATION = "gcp.storage.keyfile"

    val keyfileLocation: String?
        get() = env.getProperty(KEYFILE_LOCATION)

}