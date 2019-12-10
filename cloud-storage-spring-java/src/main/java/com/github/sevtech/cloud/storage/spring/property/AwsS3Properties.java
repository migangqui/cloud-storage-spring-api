package com.github.sevtech.cloud.storage.spring.property;

import org.springframework.core.env.Environment;

public class AwsS3Properties {
	
	private static final String S3_ACCESS_KEY = "aws.s3.accessKey";
	private static final String S3_SECRET_KEY = "aws.s3.secretKey";
	private static final String REGION = "aws.region";

	private static final String LOCALSTACK_ENABLED = "localstack.enabled";
	private static final String LOCALSTACK_ENDPOINT = "localstack.endpoint";
	private static final String LOCALSTACK_REGION = "localstack.region";

	private Environment env;

	public AwsS3Properties(Environment env) {
		this.env = env;
	}

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
