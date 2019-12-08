package com.github.migangqui.cloud.storage.spring.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.github.migangqui.cloud.storage.spring.config.AwsS3Config.AwsS3Condition
import com.github.migangqui.cloud.storage.spring.property.AwsS3Properties
import com.github.migangqui.cloud.storage.spring.service.StorageService
import com.github.migangqui.cloud.storage.spring.service.impl.AwsS3Service
import mu.KotlinLogging
import org.springframework.context.annotation.*
import org.springframework.core.env.Environment
import org.springframework.core.type.AnnotatedTypeMetadata

@Configuration
@Conditional(AwsS3Condition::class)
class AwsS3Config() {

    private val log = KotlinLogging.logger {}

    @Bean
    fun awsS3Properties(env: Environment): AwsS3Properties {
        return AwsS3Properties(env)
    }

    @Bean
    fun awsS3Client(awsS3Properties: AwsS3Properties): AmazonS3? {
        return if (awsS3Properties.isLocalstackEnabled) {
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
                            awsS3Properties.s3AccessKey,
                            awsS3Properties.s3SecretKey
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

    internal class AwsS3Condition : Condition {
        override fun matches(
            conditionContext: ConditionContext,
            annotatedTypeMetadata: AnnotatedTypeMetadata
        ): Boolean {
            return conditionContext.environment.getProperty("aws.s3.enabled") != null
        }
    }
}