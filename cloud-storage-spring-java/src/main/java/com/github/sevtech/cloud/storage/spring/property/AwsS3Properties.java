package com.github.sevtech.cloud.storage.spring.property;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;

@RequiredArgsConstructor
public class AwsS3Properties {
	
	private static final String S3_ACCESS_KEY = "aws.s3.accessKey";
	private static final String S3_SECRET_KEY = "aws.s3.secretKey";
	private static final String REGION = "aws.region";

	private static final String LOCALSTACK_ENABLED = "localstack.enabled";
	private static final String LOCALSTACK_ENDPOINT = "localstack.endpoint";
	private static final String LOCALSTACK_REGION = "localstack.region";

	private final Environment env;

	public String getS3AccessKey() {
		return env.getProperty(S3_ACCESS_KEY);
	}
	public String getS3SecretKey() {
		return env.getProperty(S3_SECRET_KEY);
	}
	public String getRegion() {
		return env.getProperty(REGION);
	}
	public Boolean isLocalstackEnabled() {
		return env.getProperty(LOCALSTACK_ENABLED) != null ? env.getProperty(LOCALSTACK_ENABLED, Boolean.class) : Boolean.FALSE;
	}
	public String getLocalstackEndpoint() {
		return env.getProperty(LOCALSTACK_ENDPOINT);
	}
	public String getLocalstackRegion() {
		return env.getProperty(LOCALSTACK_REGION);
	}

}
