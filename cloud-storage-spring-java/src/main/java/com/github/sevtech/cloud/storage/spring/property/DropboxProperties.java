package com.github.sevtech.cloud.storage.spring.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "dropbox")
public class DropboxProperties {

    private String accessToken;
    private String clientIdentifier;

}
