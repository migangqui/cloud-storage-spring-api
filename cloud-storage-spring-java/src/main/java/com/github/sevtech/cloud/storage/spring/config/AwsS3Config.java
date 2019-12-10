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
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;

@Slf4j
@Configuration
@Conditional(AwsS3Config.AwsS3Condition.class)
public class AwsS3Config {

    @Bean
    public AwsS3Properties awsS3Properties(Environment env) {
        return new AwsS3Properties(env);
    }

    @Bean
    public AmazonS3 awsS3Client(AwsS3Properties awsS3Properties) {
        AmazonS3 client;

        if (awsS3Properties.isLocalstackEnabled()) {
            log.info("Registering AmazonS3Client (with Localstack)");
            client = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(new EndpointConfiguration(awsS3Properties.getLocalstackEndpoint(), awsS3Properties.getLocalstackRegion()))
                    .withPathStyleAccessEnabled(true)
                    .build();
        } else {
            log.info("Registering AmazonS3Client");
            client = AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsS3Properties.getS3AccessKey(), awsS3Properties.getS3SecretKey())))
                    .withRegion(awsS3Properties.getRegion())
                    .build();
        }
        return client;
    }

    @Bean
    public StorageService awsS3Service(AmazonS3 awsS3Client) {
        return new AwsS3Service(awsS3Client);
    }

    static class AwsS3Condition implements Condition {
        @Override
        public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
            Boolean condition = conditionContext.getEnvironment().getProperty("aws.s3.enabled", Boolean.class);
            return condition != null && condition;
        }
    }

}
