package com.github.sevtech.cloud.storage.spring.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.github.sevtech.cloud.storage.spring.property.AwsS3Properties;
import com.github.sevtech.cloud.storage.spring.service.StorageService;
import com.github.sevtech.cloud.storage.spring.service.impl.AwsS3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;

@Slf4j
@Configuration
@ConditionalOnCloudStorageProperty(value = "aws.s3.enabled")
public class AwsS3Config {

    @Bean
    public AwsS3Properties awsS3Properties() {
        return new AwsS3Properties();
    }

    @Bean
    public AmazonS3 awsS3Client(final AwsS3Properties awsS3Properties) {
        final AmazonS3 client;

        if (awsS3Properties.getLocalstackEnabled()) {
            log.info("Registering AmazonS3Client (with Localstack)");
            client = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(new EndpointConfiguration(
                            awsS3Properties.getLocalstackEndpoint(), awsS3Properties.getLocalstackRegion()))
                    .withPathStyleAccessEnabled(true)
                    .build();
        } else {
            log.info("Registering AmazonS3Client");
            client = AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                            awsS3Properties.getAccessKey(), awsS3Properties.getSecretKey())))
                    .withRegion(awsS3Properties.getRegion())
                    .build();
        }
        return client;
    }

    @Bean
    public StorageService awsS3Service(final AmazonS3 awsS3Client) {
        return new AwsS3Service(awsS3Client);
    }

}
