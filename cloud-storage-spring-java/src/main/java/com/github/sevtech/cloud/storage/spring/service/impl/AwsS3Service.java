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
import com.github.sevtech.cloud.storage.spring.model.DeleteFileRequest;
import com.github.sevtech.cloud.storage.spring.model.DeleteFileResponse;
import com.github.sevtech.cloud.storage.spring.model.GetFileRequest;
import com.github.sevtech.cloud.storage.spring.model.GetFileResponse;
import com.github.sevtech.cloud.storage.spring.model.UploadFileRequest;
import com.github.sevtech.cloud.storage.spring.model.UploadFileResponse;
import com.github.sevtech.cloud.storage.spring.exception.NoBucketException;
import com.github.sevtech.cloud.storage.spring.service.AbstractStorageService;
import com.github.sevtech.cloud.storage.spring.service.StorageService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AwsS3Service extends AbstractStorageService implements StorageService {

    @Value("${aws.s3.bucket.name}")
    private String defaultBucketName;

    private final AmazonS3 awsS3Client;

    @Override
    public UploadFileResponse uploadFile(final UploadFileRequest request) {
        try {
            final InputStream streamToUpload = clone(request.getStream());

            final ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(IOUtils.toByteArray(request.getStream()).length);

            if (StringUtils.hasText(request.getContentType())) {
                metadata.setContentType(request.getContentType());
                metadata.setCacheControl("s-maxage");
            }

            final PutObjectRequest putObjectRequest = new PutObjectRequest(
                    getBucketName(request.getBucketName(), defaultBucketName),
                    getFilePath(request), streamToUpload, metadata).withCannedAcl(request.getAccessControl());

            log.debug("Uploading file to {}", getFilePath(request));

            awsS3Client.putObject(putObjectRequest);

            return UploadFileResponse.builder().fileName(request.getName()).status(HttpStatus.SC_OK).build();
        } catch (final AmazonServiceException ase) {
            showAmazonServiceExceptionUploadFileLogs(ase);
            return UploadFileResponse.builder()
                    .fileName(request.getName()).status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause(ase.getErrorMessage()).exception(ase).build();
        } catch (final AmazonClientException ace) {
            showAmazonClientExceptionUploadFileLogs(ace);
            return UploadFileResponse.builder().fileName(request.getName()).status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause(ace.getMessage()).exception(ace).build();
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            return UploadFileResponse.builder().fileName(request.getName()).status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause(e.getMessage()).exception(e).build();
        }
    }

    @Async
    @Override
    public Future<UploadFileResponse> uploadFileAsync(final UploadFileRequest request) {
        return new AsyncResult<>(uploadFile(request));
    }

    @Override
    public GetFileResponse getFile(final GetFileRequest request) {
        log.info("Reading file from AmazonS3 {}", request.getPath());
        try (final S3Object s3Object = awsS3Client.getObject(new GetObjectRequest(
                getBucketName(request.getBucketName(), defaultBucketName), request.getPath()))) {
            final byte[] file = IOUtils.toByteArray(s3Object.getObjectContent());
            return GetFileResponse.builder().content(file).status(HttpStatus.SC_OK).build();
        } catch (NoBucketException | IOException e) {
            log.error(e.getMessage(), e);
            return GetFileResponse.builder().cause(e.getMessage()).exception(e)
                    .status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public DeleteFileResponse deleteFile(final DeleteFileRequest request) {
        log.info("Deleting file from path {}", request.getPath());
        try {
            final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(
                    getBucketName(request.getBucketName(), defaultBucketName), request.getPath());
            awsS3Client.deleteObject(deleteObjectRequest);
            return DeleteFileResponse.builder().result(true).status(HttpStatus.SC_OK).build();
        } catch (final AmazonServiceException ase) {
            showAmazonServiceExceptionUploadFileLogs(ase);
            return DeleteFileResponse.builder().cause(ase.getMessage()).exception(ase)
                    .status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        } catch (final AmazonClientException ace) {
            showAmazonClientExceptionUploadFileLogs(ace);
            return DeleteFileResponse.builder().cause(ace.getMessage()).exception(ace)
                    .status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            return DeleteFileResponse.builder().cause(e.getMessage()).exception(e)
                    .status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    /* Private methods */

    private void showAmazonServiceExceptionUploadFileLogs(final AmazonServiceException ase) {
        log.error("Caught an AmazonServiceException, which means your request made it "
                + "to Amazon S3, but was rejected with an error response for some reason.");
        log.error("Error Message:    {}", ase.getMessage());
        log.error("HTTP Status Code: {}", ase.getStatusCode());
        log.error("AWS Error Code:   {}", ase.getErrorCode());
        log.error("Error Type:       {}", ase.getErrorType());
        log.error("Request ID:       {}", ase.getRequestId());
    }

    private void showAmazonClientExceptionUploadFileLogs(final AmazonClientException ace) {
        log.error("Caught an AmazonClientException, which means the client encountered "
                + "an internal error while trying to communicate with S3, such as not being able to access the network.");
        log.error("Error Message: " + ace.getMessage());
    }
}



