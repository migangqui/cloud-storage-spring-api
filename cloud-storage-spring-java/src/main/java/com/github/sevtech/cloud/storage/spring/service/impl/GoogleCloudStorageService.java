package com.github.sevtech.cloud.storage.spring.service.impl;

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
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import java.io.IOException;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
public class GoogleCloudStorageService extends AbstractStorageService implements StorageService {

    @Value("${gcp.storage.bucket.name}")
    private String defaultBucketName;

    private final Storage storageClient;

    @Override
    public UploadFileResponse uploadFile(final UploadFileRequest uploadFileRequest) {
        try {
            final String bucketName = getBucketName(uploadFileRequest.getBucketName(), defaultBucketName);

            final BlobInfo blobInfo =
                    storageClient.create(BlobInfo.newBuilder(bucketName, getFilePath(uploadFileRequest))
//                    .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
                    .build(), IOUtils.toByteArray(uploadFileRequest.getStream()));
            return UploadFileResponse.builder().fileName(uploadFileRequest.getName())
                    .status(HttpStatus.SC_OK).comment(blobInfo.getMediaLink()).build();
        } catch (NoBucketException | IOException e) {
            log.warn("Error creating blob");
            return UploadFileResponse.builder().fileName(uploadFileRequest.getName())
                    .status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause("Error creating blob").exception(e).build();
        }
    }

    @Async
    @Override
    public Future<UploadFileResponse> uploadFileAsync(final UploadFileRequest uploadFileRequest) {
        return new AsyncResult<>(uploadFile(uploadFileRequest));
    }

    @Override
    public GetFileResponse getFile(final GetFileRequest request) {
        log.info("Reading file from AmazonS3 {}", request.getPath());
        try {
            final byte[] file = storageClient.readAllBytes(
                    BlobId.of(getBucketName(request.getBucketName(), defaultBucketName), request.getPath()));
            return GetFileResponse.builder().content(file).status(HttpStatus.SC_OK).build();
        } catch (final NoBucketException e) {
            log.error(e.getMessage(), e);
            return GetFileResponse.builder().cause(e.getMessage()).exception(e)
                    .status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public DeleteFileResponse deleteFile(DeleteFileRequest request) {
        log.info("Deleting file from path {}", request.getPath());
        try {
            storageClient.delete(
                    BlobId.of(getBucketName(request.getBucketName(), defaultBucketName), request.getPath()));
            return DeleteFileResponse.builder().result(true).status(HttpStatus.SC_OK).build();
        } catch (NoBucketException e) {
            log.error(e.getMessage(), e);
            return DeleteFileResponse.builder().cause(e.getMessage()).exception(e)
                    .status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }

}
