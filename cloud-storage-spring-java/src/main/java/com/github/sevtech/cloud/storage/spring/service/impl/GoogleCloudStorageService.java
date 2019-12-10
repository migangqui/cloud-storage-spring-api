package com.github.sevtech.cloud.storage.spring.service.impl;

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
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.Future;

@Slf4j
public class GoogleCloudStorageService extends AbstractStorageService implements StorageService {

    @Value("${gcp.storage.bucket.name}")
    private String defaultBucketName;

    private Storage storageClient;

    public GoogleCloudStorageService(Storage storageClient) {
        this.storageClient = storageClient;
    }

    @Override
    public UploadFileResponse uploadFile(UploadFileRequest uploadFileRequest) {
        UploadFileResponse result;

        try {
            final String bucketName = getBucketName(uploadFileRequest.getBucketName(), defaultBucketName);

            final BlobInfo blobInfo = storageClient.create(BlobInfo.newBuilder(bucketName, getFilePath(uploadFileRequest))
//                    .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
                    .build(), IOUtils.toByteArray(uploadFileRequest.getStream()));
            result = UploadFileResponse.builder().fileName(uploadFileRequest.getName()).status(HttpStatus.SC_OK).comment(blobInfo.getMediaLink()).build();
        } catch (NoBucketException | IOException e) {
            log.warn("Error creating blob");
            result = UploadFileResponse.builder().fileName(uploadFileRequest.getName()).status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause("Error creating blob").exception(e).build();
        }
        return result;
    }

    @Async
    @Override
    public Future<UploadFileResponse> uploadFileAsync(UploadFileRequest uploadFileRequest) {
        return new AsyncResult<>(uploadFile(uploadFileRequest));
    }

    @Override
    public GetFileResponse getFile(GetFileRequest request) {
        log.info("Reading file from AmazonS3 {}", request.getPath());
        GetFileResponse result;
        try {
            final byte[] file = storageClient.readAllBytes(BlobId.of(getBucketName(request.getBucketName(), defaultBucketName), request.getPath()));
            result = GetFileResponse.builder().content(new ByteArrayInputStream(file)).status(HttpStatus.SC_OK).build();
        } catch (NoBucketException e) {
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
            storageClient.delete(BlobId.of(getBucketName(request.getBucketName(), defaultBucketName), request.getPath()));
            result = DeleteFileResponse.builder().result(true).status(HttpStatus.SC_OK).build();
        } catch (NoBucketException e) {
            log.error(e.getMessage(), e);
            result = DeleteFileResponse.builder().cause(e.getMessage()).exception(e).status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
        return result;
    }

}
