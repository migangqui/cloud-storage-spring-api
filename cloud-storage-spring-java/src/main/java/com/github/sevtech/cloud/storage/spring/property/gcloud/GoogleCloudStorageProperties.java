package com.github.sevtech.cloud.storage.spring.property.gcloud;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "gcp.storage")
public class GoogleCloudStorageProperties {

	private String keyfile;

}
