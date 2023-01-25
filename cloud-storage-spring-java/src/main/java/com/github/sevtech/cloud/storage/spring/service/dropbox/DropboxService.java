package com.github.sevtech.cloud.storage.spring.service.dropbox;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.github.sevtech.cloud.storage.spring.model.DeleteFileRequest;
import com.github.sevtech.cloud.storage.spring.model.DeleteFileResponse;
import com.github.sevtech.cloud.storage.spring.model.GetFileRequest;
import com.github.sevtech.cloud.storage.spring.model.GetFileResponse;
import com.github.sevtech.cloud.storage.spring.model.UploadFileRequest;
import com.github.sevtech.cloud.storage.spring.model.UploadFileResponse;
import com.github.sevtech.cloud.storage.spring.service.AbstractStorageService;
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
public class DropboxService extends AbstractStorageService implements StorageService {

    private final DbxClientV2 dbxClientV2;

    @Override
    public UploadFileResponse uploadFile(final UploadFileRequest request) {
        try {
            log.info("Uploading file to {}", getFilePath(request));
            dbxClientV2.files().uploadBuilder("/" + getFilePath(request))
                    .uploadAndFinish(request.getStream());
            return UploadFileResponse.builder().fileName(request.getName()).status(HttpStatus.SC_OK).build();
        } catch (final DbxException | IOException e) {
            return UploadFileResponse.builder().fileName(request.getName()).status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause(e.getLocalizedMessage()).exception(e).build();
        }
    }

    @Async
    @Override
    public Future<UploadFileResponse> uploadFileAsync(final UploadFileRequest request) {
        return new AsyncResult<>(uploadFile(request));
    }

    @Override
    public GetFileResponse getFile(final GetFileRequest request) {
        try {
            log.info("Reading file from {}", request.getPath());
            final DbxDownloader<FileMetadata> download = dbxClientV2.files().download(request.getPath());
            return GetFileResponse.builder().content(
                    new byte[download.getInputStream().available()]).status(HttpStatus.SC_OK).build();
        } catch (final DbxException | IOException e) {
            return GetFileResponse.builder().status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause(e.getLocalizedMessage()).exception(e).build();
        }
    }

    @Override
    public DeleteFileResponse deleteFile(final DeleteFileRequest request) {
        try {
            log.info("Deleting file from path {}", request.getPath());
            dbxClientV2.files().deleteV2(request.getPath());
            return DeleteFileResponse.builder().result(true).status(HttpStatus.SC_OK).build();
        } catch (final DbxException e) {
            return DeleteFileResponse.builder().status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause(e.getLocalizedMessage()).exception(e).build();
        }
    }
}
