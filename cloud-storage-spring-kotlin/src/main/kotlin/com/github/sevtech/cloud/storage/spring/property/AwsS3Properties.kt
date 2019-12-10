package com.github.sevtech.cloud.storage.spring.property

import org.springframework.core.env.Environment

class AwsS3Properties(private val env: Environment) {

    private val BUCKET_NAME = "amazon.s3.bucket.name"
    private val S3_ACCESS_KEY = "amazon.s3.accessKey"
    private val S3_SECRET_KEY = "amazon.s3.secretKey"
    private val REGION = "amazon.region"

    private val LOCALSTACK_ENABLED = "localstack.enabled"
    private val LOCALSTACK_ENDPOINT = "localstack.endpoint"
    private val LOCALSTACK_REGION = "localstack.region"

    val bucketName: String?
        get() = env.getProperty(BUCKET_NAME)
    val s3AccessKey: String?
        get() = env.getProperty(S3_ACCESS_KEY)
    val s3SecretKey: String?
        get() = env.getProperty(S3_SECRET_KEY)
    val region: String?
        get() = env.getProperty(REGION)
    val isLocalstackEnabled: Boolean
        get() =
            if (env.getProperty(LOCALSTACK_ENABLED) != null) env.getProperty(LOCALSTACK_ENABLED, Boolean::class.java)!! else java.lang.Boolean.FALSE
    val localstackEndpoint: String?
        get() = env.getProperty(LOCALSTACK_ENDPOINT)
    val localstackRegion: String?
        get() = env.getProperty(LOCALSTACK_REGION)
}