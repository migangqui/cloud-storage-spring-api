package com.github.sevtech.cloud.storage.spring.service.impl;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.github.sevtech.cloud.storage.spring.bean.*;
import com.github.sevtech.cloud.storage.spring.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
public class DropboxService implements StorageService {

    private final DbxClientV2 dbxClientV2;

    @Override
    public UploadFileResponse uploadFile(UploadFileRequest request) {
        UploadFileResponse result;
        try {
            FileMetadata metadata = dbxClientV2.files().uploadBuilder("/" + request.getFolder() + "/" + request.getName())
                    .uploadAndFinish(request.getStream());
            result = UploadFileResponse.builder().fileName(request.getName()).status(HttpStatus.SC_OK).build();
        } catch (DbxException | IOException e) {
            result = UploadFileResponse.builder().fileName(request.getName()).status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause(e.getLocalizedMessage()).exception(e).build();
        }
        return result;
    }

    @Override
    public Future<UploadFileResponse> uploadFileAsync(UploadFileRequest request) {
        return null;
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
