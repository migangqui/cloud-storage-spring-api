package com.github.sevtech.cloud.storage.spring.property.aws

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "aws.s3")
class AwsS3Properties {
    var accessKey: String? = null
    var secretKey: String? = null
    var region: String? = null
    
    var localstackEnabled: Boolean = false
    var localstackEndpoint: String? = null
    var localstackRegion: String? = null
}