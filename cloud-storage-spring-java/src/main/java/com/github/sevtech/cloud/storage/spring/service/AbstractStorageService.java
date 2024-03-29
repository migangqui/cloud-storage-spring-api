package com.github.sevtech.cloud.storage.spring.service;

import com.github.sevtech.cloud.storage.spring.exception.NoBucketException;
import com.github.sevtech.cloud.storage.spring.model.UploadFileRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Optional;

@Slf4j
public abstract class AbstractStorageService {

    protected InputStream clone(final InputStream inputStream) {
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
        } catch (final Exception exception) {
            log.warn("Exception cloning inputStream", exception);
        }
        return result;
    }

    protected String getBucketName(final String bucketName, final String defaultBucketName) throws NoBucketException {
        return Optional.ofNullable(
                Optional.ofNullable(bucketName).orElse(defaultBucketName))
                .orElseThrow(() -> new NoBucketException("Bucket name not indicated"));
    }

    protected String getFilePath(final UploadFileRequest request) {
        return request.getFolder().concat("/").concat(request.getName());
    }

}
