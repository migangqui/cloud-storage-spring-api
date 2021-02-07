package com.github.sevtech.cloud.storage.spring.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aws.s3")
public class AwsS3Properties {

	private String accessKey;
	private String secretKey;
	private String region;

	private Boolean localstackEnabled;
	private String localstackEndpoint;
	private String localstackRegion;
}
