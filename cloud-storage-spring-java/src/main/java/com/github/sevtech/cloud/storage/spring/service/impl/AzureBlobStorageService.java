package com.github.sevtech.cloud.storage.spring.service.impl;

import com.amazonaws.util.IOUtils;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlockBlobItem;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.github.sevtech.cloud.storage.spring.bean.DeleteFileRequest;
import com.github.sevtech.cloud.storage.spring.bean.DeleteFileResponse;
import com.github.sevtech.cloud.storage.spring.bean.GetFileRequest;
import com.github.sevtech.cloud.storage.spring.bean.GetFileResponse;
import com.github.sevtech.cloud.storage.spring.bean.UploadFileRequest;
import com.github.sevtech.cloud.storage.spring.bean.UploadFileResponse;
import com.github.sevtech.cloud.storage.spring.exception.NoBucketException;
import com.github.sevtech.cloud.storage.spring.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.AsyncResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.Future;

@Slf4j
public class AzureBlobStorageService implements StorageService {

    @Value("${azure.blob.storage.container.name}")
    private String defaultContainerName;

    private final BlobContainerClient blobContainerClient;

    public AzureBlobStorageService(BlobContainerClient blobContainerClient) {
        this.blobContainerClient = blobContainerClient;
    }

    @Override
    public UploadFileResponse uploadFile(UploadFileRequest uploadFileRequest) {
        UploadFileResponse result;

        try {
            final String containerName = Optional.ofNullable(
                    Optional.ofNullable(uploadFileRequest.getBucketName()).orElse(defaultContainerName))
                    .orElseThrow(() -> new NoBucketException("Bucket name not indicated"));

            final String path = uploadFileRequest.getFolder().concat("/").concat(uploadFileRequest.getName());

            InputStream streamToUpload = clone(uploadFileRequest.getStream());

            final BlockBlobClient blockBlobClient = blobContainerClient.getBlobClient(path).getBlockBlobClient();

            BlockBlobItem blobItem = blockBlobClient.upload(streamToUpload, IOUtils.toByteArray(uploadFileRequest.getStream()).length);

            result = UploadFileResponse.builder().fileName(uploadFileRequest.getName()).status(HttpStatus.SC_OK).build();

        } catch (NoBucketException | IOException e) {
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

    private InputStream clone(final InputStream inputStream) {
        InputStream result = null;
        try {
            inputStream.mark(0);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int readLength;
            while ((readLength = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, readLength);
            }
            inputStream.reset();
            outputStream.flush();
            result = new ByteArrayInputStream(outputStream.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}