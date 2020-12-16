package com.github.sevtech.cloud.storage.spring.service.impl;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.github.sevtech.cloud.storage.spring.bean.*;
import com.github.sevtech.cloud.storage.spring.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import java.io.IOException;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
public class DropboxService implements StorageService {

    private final DbxClientV2 dbxClientV2;

    @Override
    public UploadFileResponse uploadFile(final UploadFileRequest request) {
        UploadFileResponse result;
        try {
            dbxClientV2.files().uploadBuilder("/" + request.getFolder() + "/" + request.getName())
                    .uploadAndFinish(request.getStream());
            result = UploadFileResponse.builder().fileName(request.getName()).status(HttpStatus.SC_OK).build();
        } catch (DbxException | IOException e) {
            result = UploadFileResponse.builder().fileName(request.getName()).status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause(e.getLocalizedMessage()).exception(e).build();
        }
        return result;
    }

    @Async
    @Override
    public Future<UploadFileResponse> uploadFileAsync(final UploadFileRequest request) {
        return new AsyncResult<>(uploadFile(request));
    }

    @Override
    public GetFileResponse getFile(final GetFileRequest request) {
        GetFileResponse result;
        try {
            DbxDownloader<FileMetadata> download = dbxClientV2.files().download(request.getPath());
            result = GetFileResponse.builder().content(new byte[download.getInputStream().available()]).status(HttpStatus.SC_OK).build();
        } catch (DbxException | IOException e) {
            result = GetFileResponse.builder().status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause(e.getLocalizedMessage()).exception(e).build();
        }
        return result;
    }

    @Override
    public DeleteFileResponse deleteFile(final DeleteFileRequest request) {
        DeleteFileResponse result;
        try {
            dbxClientV2.files().deleteV2(request.getPath());
            result = DeleteFileResponse.builder().result(true).status(HttpStatus.SC_OK).build();
        } catch (DbxException e) {
            result = DeleteFileResponse.builder().status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause(e.getLocalizedMessage()).exception(e).build();
        }
        return result;
    }
}
