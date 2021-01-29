package com.github.sevtech.cloud.storage.spring.bean;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.io.InputStream;

@Getter
@Builder
public class UploadFileRequest {
    @NonNull
    private InputStream stream;
    @NonNull
    private String folder;
    @NonNull
    private String name;
    @NonNull
    private String contentType;
    private String bucketName;
    @Builder.Default
    private CannedAccessControlList accessControl = CannedAccessControlList.Private;
}
