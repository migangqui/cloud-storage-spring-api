package com.github.sevtech.cloud.storage.spring.property;

import org.springframework.core.env.Environment;

public class AzureBlobStorageProperties {

    private static final String CONNECTION_STRING = "azure.blob.storage.connectionString";
    private static final String CONTAINER_NAME = "azure.blob.storage.container.name";

    private Environment env;

    public AzureBlobStorageProperties(Environment env) { this.env = env; }

    public String getConnectionString() { return env.getProperty(CONNECTION_STRING); }

    public String getContainerName() { return env.getProperty(CONTAINER_NAME); }

}
