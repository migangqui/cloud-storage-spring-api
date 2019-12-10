package com.github.sevtech.cloud.storage.spring.property;

import org.springframework.core.env.Environment;

public class GoogleCloudStorageProperties {

	private static final String KEYFILE_LOCATION = "gcp.storage.keyfile";

	private Environment env;

	public GoogleCloudStorageProperties(Environment env) {
		this.env = env;
	}

	public String getKeyFileLocation() {
		return env.getProperty(KEYFILE_LOCATION);
	}
}
