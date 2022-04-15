package com.github.sevtech.cloud.storage.spring.service.impl;

import com.amazonaws.util.IOUtils;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.specialized.BlockBlobClient;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
public class AzureBlobStorageService extends AbstractStorageService implements StorageService {

    @Value("${azure.blob.storage.container.name}")
    private String defaultContainerName;

    private final BlobServiceClient blobServiceClient;

    @Override
    public UploadFileResponse uploadFile(final UploadFileRequest uploadFileRequest) {
        try {
            final InputStream streamToUpload = clone(uploadFileRequest.getStream());

            final BlockBlobClient blockBlobClient = getBlobClient(uploadFileRequest.getBucketName(), getFilePath(uploadFileRequest));

            final BlobHttpHeaders headers = new BlobHttpHeaders();
            headers.setContentType(uploadFileRequest.getContentType());

            blockBlobClient.upload(streamToUpload, IOUtils.toByteArray(uploadFileRequest.getStream()).length);
            blockBlobClient.setHttpHeaders(headers);

            return UploadFileResponse.builder().fileName(uploadFileRequest.getName())
                    .status(HttpStatus.SC_OK).comment(blockBlobClient.getBlobUrl()).build();
        } catch (IOException | NoBucketException e) {
            log.warn("Error creating blob");
            return UploadFileResponse.builder().fileName(uploadFileRequest.getName())
                    .status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause("Error creating blob").exception(e).build();
        }
    }

    @Async
    @Override
    public Future<UploadFileResponse> uploadFileAsync(final UploadFileRequest request) {
        return new AsyncResult<>(uploadFile(request));
    }

    @Override
    public GetFileResponse getFile(final GetFileRequest request) {
        log.info("Reading file from Azure {}", request.getPath());
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            final BlockBlobClient blockBlobClient = getBlobClient(request.getBucketName(), request.getPath());
            blockBlobClient.download(outputStream);
            return GetFileResponse.builder().content(outputStream.toByteArray()).status(HttpStatus.SC_OK).build();
        } catch (final IOException | NoBucketException e) {
            log.error(e.getMessage(), e);
            return GetFileResponse.builder().cause(e.getMessage()).exception(e)
                    .status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public DeleteFileResponse deleteFile(final DeleteFileRequest request) {
        log.info("Deleting file from Azure {}", request.getPath());
        try {
            final BlockBlobClient blockBlobClient = getBlobClient(request.getBucketName(), request.getPath());
            blockBlobClient.delete();
            return DeleteFileResponse.builder().status(HttpStatus.SC_OK).build();
        } catch (NoBucketException e) {
            log.error(e.getMessage(), e);
            return DeleteFileResponse.builder().cause(e.getMessage()).exception(e)
                    .status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    private BlockBlobClient getBlobClient(final String bucketName, final String path) throws NoBucketException {
        return blobServiceClient.getBlobContainerClient(getBucketName(bucketName, defaultContainerName))
                .getBlobClient(path).getBlockBlobClient();
    }
}
