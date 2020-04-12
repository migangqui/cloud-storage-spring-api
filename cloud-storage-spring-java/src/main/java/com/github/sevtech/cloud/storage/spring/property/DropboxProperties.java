package com.github.sevtech.cloud.storage.spring.property;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;

@RequiredArgsConstructor
public class DropboxProperties {

	private final Environment env;

	private static final String ACCESS_TOKEN = "dropbox.accessToken";
	private static final String CLIENT_IDENTIFIER = "dropbox.clientIdentifier";

	public String getDropboxAccessToken() {
		return env.getProperty(ACCESS_TOKEN);
	}

	public String getClientIdentifier() { return env.getProperty(CLIENT_IDENTIFIER); }

}
