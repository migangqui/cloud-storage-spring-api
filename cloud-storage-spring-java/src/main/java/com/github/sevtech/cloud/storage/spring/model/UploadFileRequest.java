package com.github.sevtech.cloud.storage.spring.model;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;

@Getter
@Builder
public class UploadFileRequest {
    private InputStream stream;
    private String folder;
    private String name;
    private String contentType;
    private String bucketName;
    @Builder.Default
    private CannedAccessControlList accessControl = CannedAccessControlList.Private;
}
