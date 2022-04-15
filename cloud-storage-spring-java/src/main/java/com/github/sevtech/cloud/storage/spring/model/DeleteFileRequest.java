package com.github.sevtech.cloud.storage.spring.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteFileRequest {
    private String path;
    private String bucketName;
}
