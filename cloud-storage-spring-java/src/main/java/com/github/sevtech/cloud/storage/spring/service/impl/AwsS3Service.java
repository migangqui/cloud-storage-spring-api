package com.github.sevtech.cloud.storage.spring.service.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.github.sevtech.cloud.storage.spring.bean.DeleteFileRequest;
import com.github.sevtech.cloud.storage.spring.bean.DeleteFileResponse;
import com.github.sevtech.cloud.storage.spring.bean.GetFileRequest;
import com.github.sevtech.cloud.storage.spring.bean.GetFileResponse;
import com.github.sevtech.cloud.storage.spring.bean.UploadFileRequest;
import com.github.sevtech.cloud.storage.spring.bean.UploadFileResponse;
import com.github.sevtech.cloud.storage.spring.exception.NoBucketException;
import com.github.sevtech.cloud.storage.spring.service.AbstractStorageService;
import com.github.sevtech.cloud.storage.spring.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Future;

@Slf4j
public class AwsS3Service extends AbstractStorageService implements StorageService {

    @Value("${aws.s3.bucket.name}")
    private String defaultBucketName;

    private AmazonS3 awsS3Client;

    public AwsS3Service(AmazonS3 awsS3Client) {
        this.awsS3Client = awsS3Client;
    }

    @Override
    public UploadFileResponse uploadFile(UploadFileRequest request) {
        UploadFileResponse result;

        try {
            InputStream streamToUpload = clone(request.getStream());

            final ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(IOUtils.toByteArray(request.getStream()).length);

            if (!StringUtils.isEmpty(request.getContentType())) {
                metadata.setContentType(request.getContentType());
                metadata.setCacheControl("s-maxage");
            }

            final PutObjectRequest putObjectRequest = new PutObjectRequest(getBucketName(request.getBucketName(), defaultBucketName), getFilePath(request), streamToUpload, metadata)
                    .withCannedAcl(request.getAccessControl());

            log.debug("Uploading file to {}", getFilePath(request));

            awsS3Client.putObject(putObjectRequest);

            result = UploadFileResponse.builder().fileName(request.getName()).status(HttpStatus.SC_OK).build();
        } catch (AmazonServiceException ase) {
            showAmazonServiceExceptionUploadFileLogs(ase);
            result = UploadFileResponse.builder().fileName(request.getName()).status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause(ase.getErrorMessage()).exception(ase).build();
        } catch (AmazonClientException ace) {
            showAmazonClientExceptionUploadFileLogs(ace);
            result = UploadFileResponse.builder().fileName(request.getName()).status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause(ace.getMessage()).exception(ace).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = UploadFileResponse.builder().fileName(request.getName()).status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause(e.getMessage()).exception(e).build();
        }
        return result;
    }

    @Async
    @Override
    public Future<UploadFileResponse> uploadFileAsync(UploadFileRequest request) {
        return new AsyncResult<>(uploadFile(request));
    }

    @Override
    public GetFileResponse getFile(GetFileRequest request) {
        log.info("Reading file from AmazonS3 {}", request.getPath());
        GetFileResponse result;
        try (S3Object s3Object = awsS3Client.getObject(new GetObjectRequest(getBucketName(request.getBucketName(), defaultBucketName), request.getPath()))) {
            final byte[] file = new byte[s3Object.getObjectContent().available()];
            result = GetFileResponse.builder().content(file).status(HttpStatus.SC_OK).build();
        } catch (NoBucketException | IOException e) {
            log.error(e.getMessage(), e);
            result = GetFileResponse.builder().cause(e.getMessage()).exception(e).status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
        return result;
    }

    @Override
    public DeleteFileResponse deleteFile(DeleteFileRequest request) {
        log.info("Deleting file from path {}", request.getPath());
        DeleteFileResponse result;
        try {
            final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(getBucketName(request.getBucketName(), defaultBucketName), request.getPath());
            awsS3Client.deleteObject(deleteObjectRequest);
            result = DeleteFileResponse.builder().result(true).status(HttpStatus.SC_OK).build();
        } catch (AmazonServiceException ase) {
            showAmazonServiceExceptionUploadFileLogs(ase);
            result = DeleteFileResponse.builder().cause(ase.getMessage()).exception(ase).status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        } catch (AmazonClientException ace) {
            showAmazonClientExceptionUploadFileLogs(ace);
            result = DeleteFileResponse.builder().cause(ace.getMessage()).exception(ace).status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = DeleteFileResponse.builder().cause(e.getMessage()).exception(e).status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
        return result;
    }

    /* Private methods */

    private void showAmazonServiceExceptionUploadFileLogs(AmazonServiceException ase) {
        log.error("Caught an AmazonServiceException, which means your request made it "
                + "to Amazon S3, but was rejected with an error response for some reason.");
        log.error("Error Message:    {}", ase.getMessage());
        log.error("HTTP Status Code: {}", ase.getStatusCode());
        log.error("AWS Error Code:   {}", ase.getErrorCode());
        log.error("Error Type:       {}", ase.getErrorType());
        log.error("Request ID:       {}", ase.getRequestId());
    }

    private void showAmazonClientExceptionUploadFileLogs(AmazonClientException ace) {
        log.error("Caught an AmazonClientException, which means the client encountered "
                + "an internal error while trying to communicate with S3, such as not being able to access the network.");
        log.error("Error Message: " + ace.getMessage());
    }
}



