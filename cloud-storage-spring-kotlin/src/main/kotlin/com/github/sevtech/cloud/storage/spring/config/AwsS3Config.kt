package com.github.sevtech.cloud.storage.spring.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.github.sevtech.cloud.storage.spring.property.AwsS3Properties
import com.github.sevtech.cloud.storage.spring.service.StorageService
import com.github.sevtech.cloud.storage.spring.service.impl.AwsS3Service
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnCloudStorageProperty(value = "aws.s3.enabled")
class AwsS3Config() {

    private val log = KotlinLogging.logger {}

    @Bean
    fun awsS3Properties(): AwsS3Properties {
        return AwsS3Properties()
    }

    @Bean
    fun awsS3Client(awsS3Properties: AwsS3Properties): AmazonS3? {
        return if (awsS3Properties.localstackEnabled) {
            log.info("Registering AmazonS3Client (with Localstack)")
            AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                    EndpointConfiguration(
                        awsS3Properties.localstackEndpoint,
                        awsS3Properties.localstackRegion
                    )
                )
                .withPathStyleAccessEnabled(true)
                .build()
        } else {
            log.info("Registering AmazonS3Client")
            AmazonS3ClientBuilder
                .standard()
                .withCredentials(
                    AWSStaticCredentialsProvider(
                        BasicAWSCredentials(
                            awsS3Properties.accessKey,
                            awsS3Properties.secretKey
                        )
                    )
                )
                .withRegion(awsS3Properties.region)
                .build()
        }
    }

    @Bean
    fun awsS3Service(awsS3Client: AmazonS3): StorageService {
        return AwsS3Service(awsS3Client)
    }
}