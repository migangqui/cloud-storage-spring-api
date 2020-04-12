package com.github.sevtech.cloud.storage.spring.property;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;

@RequiredArgsConstructor
public class AzureBlobStorageProperties {

    private static final String CONNECTION_STRING = "azure.blob.storage.connectionString";
    private static final String CONTAINER_NAME = "azure.blob.storage.container.name";

    private final Environment env;

    public String getConnectionString() { return env.getProperty(CONNECTION_STRING); }

    public String getContainerName() { return env.getProperty(CONTAINER_NAME); }

}
