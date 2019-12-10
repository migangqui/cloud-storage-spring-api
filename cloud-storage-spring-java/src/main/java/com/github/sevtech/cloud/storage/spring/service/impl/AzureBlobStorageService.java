package com.github.sevtech.cloud.storage.spring.service.impl;

import com.amazonaws.util.IOUtils;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.specialized.BlockBlobClient;
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
import org.springframework.scheduling.annotation.AsyncResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Future;

@Slf4j
public class AzureBlobStorageService extends AbstractStorageService implements StorageService {

    @Value("${azure.blob.storage.container.name}")
    private String defaultContainerName;

    private final BlobServiceClient blobServiceClient;

    public AzureBlobStorageService(BlobServiceClient blobServiceClient) {
        this.blobServiceClient = blobServiceClient;
    }

    @Override
    public UploadFileResponse uploadFile(UploadFileRequest uploadFileRequest) {
        UploadFileResponse result;

        try {
            final String bucketName = getBucketName(uploadFileRequest.getBucketName(), defaultContainerName);

            final InputStream streamToUpload = clone(uploadFileRequest.getStream());

            final BlockBlobClient blockBlobClient = blobServiceClient.getBlobContainerClient(bucketName).getBlobClient(getFilePath(uploadFileRequest)).getBlockBlobClient();

            BlobHttpHeaders headers = new BlobHttpHeaders();
            headers.setContentType(uploadFileRequest.getContentType());
            blockBlobClient.upload(streamToUpload, IOUtils.toByteArray(uploadFileRequest.getStream()).length);
            blockBlobClient.setHttpHeaders(headers);

            result = UploadFileResponse.builder().fileName(uploadFileRequest.getName()).status(HttpStatus.SC_OK).comment(blockBlobClient.getBlobUrl()).build();
        } catch (IOException | NoBucketException e) {
            log.warn("Error creating blob");
            result = UploadFileResponse.builder().fileName(uploadFileRequest.getName()).status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause("Error creating blob").exception(e).build();
        }
        return result;
    }

    @Override
    public Future<UploadFileResponse> uploadFileAsync(UploadFileRequest request) {
        return new AsyncResult<>(uploadFile(request));
    }

    @Override
    public GetFileResponse getFile(GetFileRequest request) {
        return null;
    }

    @Override
    public DeleteFileResponse deleteFile(DeleteFileRequest request) {
        return null;
    }
}
