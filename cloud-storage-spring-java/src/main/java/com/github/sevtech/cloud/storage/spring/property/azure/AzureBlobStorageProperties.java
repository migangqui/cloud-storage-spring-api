package com.github.sevtech.cloud.storage.spring.property.azure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "azure.blob.storage")
public class AzureBlobStorageProperties {

    private String connectionString;
    private String containerName;

}
