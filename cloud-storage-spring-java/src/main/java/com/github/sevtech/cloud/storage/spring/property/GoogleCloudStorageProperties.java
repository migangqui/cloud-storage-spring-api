package com.github.sevtech.cloud.storage.spring.property;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;

@RequiredArgsConstructor
public class GoogleCloudStorageProperties {

	private static final String KEYFILE_LOCATION = "gcp.storage.keyfile";

	private final Environment env;

	public String getKeyFileLocation() {
		return env.getProperty(KEYFILE_LOCATION);
	}
}
