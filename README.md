# cloud-storage-spring-api (Java/Kotlin)

Here we are a Java and a Kotlin API to manage files of AWS S3, Google Cloud Storage and Azure Blob Storage in Spring framework. In order to use it, are necesaries the following steps:

### Add dependency to pom.xml:

If you use Java (<https://mvnrepository.com/artifact/com.github.migangqui/spring-aws-s3-java>):

```xml
<dependency>
	<groupId>com.github.migangqui</groupId>
	<artifactId>spring-aws-s3-java</artifactId>
	<version>${currentVersion}</version>
</dependency>
```

If you use Kotlin (<https://mvnrepository.com/artifact/com.github.migangqui/spring-aws-s3-kotlin>):

```xml
<dependency>
	<groupId>com.github.migangqui</groupId>
	<artifactId>spring-aws-s3-kotlin</artifactId>
	<version>${currentVersion}</version>
</dependency>
```

```${currentVersion}``` right now is ```1.1.0```

Both them are in Maven Central.

### Register the following properties in your application.yml:

```yaml
amazon:
    s3:
        accessKey: [AMAZON_ACCESS_KEY]
        secretKey: [AMAZON_SECRET_KEY]
        bucket.name: example-bucket-s3
    region: [GovCloud("us-gov-west-1"),
               US_EAST_1("us-east-1"),
               US_WEST_1("us-west-1"),
               US_WEST_2("us-west-2"),
               EU_WEST_1("eu-west-1"),
               EU_CENTRAL_1("eu-central-1"),
               AP_SOUTH_1("ap-south-1"),
               AP_SOUTHEAST_1("ap-southeast-1"),
               AP_SOUTHEAST_2("ap-southeast-2"),
               AP_NORTHEAST_1("ap-northeast-1"),
               AP_NORTHEAST_2("ap-northeast-2"),
               SA_EAST_1("sa-east-1"),
               CN_NORTH_1("cn-north-1")]**
```
** Only one and only the string of the region.

## Enable async

Add ```@EnableAsync``` annotation in your Spring Application class to enable async upload method.

## File size

To controle max size of files you can upload, set the following properties:
```yaml
spring:
    servlet:
        multipart:
            max-file-size: 128KB
            max-request-size: 128KB
```

## Localstack support

This library can be tested with Localstack (<https://github.com/localstack/localstack>).
You only have to set the following properties in your application.yml:

```yaml
localstack:
  enabled: false (by default)
  endpoint: http://localhost:4572
  region: us-east-1
```

In order to run easily Localstack, I have added ```docker-compose.yml``` file to the folder ```localstack```. 
You have run the command ```docker-compose up``` to make it work.

I hardly recommend install AWS CLI in your local. It helps you to manage the buckets to run the tests with Localstack.
Here you are the documentation to install the version 2: <https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html>

To create a local bucket you must run this command `aws2 --endpoint-url=http://localhost:4572 s3 mb s3://mytestbucket`

To check out if the bucket has been created run this command `aws2 --endpoint-url=http://localhost:4572 s3 ls`

When you create a bucket, you have to add `yourbucketname.localhost` to your hosts local file mapped to `127.0.0.1`.

Here we are the AWS CLI S3 command options: <https://docs.aws.amazon.com/en_en/cli/latest/userguide/cli-services-s3-commands.html>

## How to use

You have to inject ```AmazonS3Service``` as dependency in your Spring component.
The service provide these methods:

##### Java
```java
public interface AmazonS3Service {
	
    UploadFileResponse uploadFile(UploadFileRequest uploadFileRequest);
    
    Future<UploadFileResponse> uploadFileAsync(UploadFileRequest uploadFileRequest);

    InputStream getFile(String path);
    
    boolean deleteFile(String path);

}
```
##### Kotlin
```kotlin
interface AmazonS3Service {
    
    fun uploadFile(uploadFileRequest: UploadFileRequest): UploadFileResponse

    fun uploadFileAsync(uploadFileRequest: UploadFileRequest): Future<UploadFileResponse>

    fun getFile(path: String): InputStream

    fun deleteFile(path: String): Boolean

}
```

## License

This project is licensed under the MIT License - see the LICENSE.md file for details

## Improvements
###v1.1.0
* Improve configuration
* Not component scan necesary
* Refactor code
* Chose bucket name dinamically

## Next improvements

In future versions is planned include:
* AWS S3 File access configuration (nowadays is Public Read by default)
* Unit tests
* ... 