package com.github.migangqui.cloud.storage.spring.service;

import com.github.migangqui.cloud.storage.spring.bean.*;

import java.io.InputStream;
import java.util.concurrent.Future;

public interface StorageService {

	UploadFileResponse uploadFile(UploadFileRequest request);

	Future<UploadFileResponse> uploadFileAsync(UploadFileRequest request);

	GetFileResponse getFile(GetFileRequest request);

	DeleteFileResponse deleteFile(DeleteFileRequest request);

}
