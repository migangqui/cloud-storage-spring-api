package com.github.sevtech.cloud.storage.spring.service;

import com.github.sevtech.cloud.storage.spring.model.DeleteFileRequest;
import com.github.sevtech.cloud.storage.spring.model.DeleteFileResponse;
import com.github.sevtech.cloud.storage.spring.model.GetFileRequest;
import com.github.sevtech.cloud.storage.spring.model.GetFileResponse;
import com.github.sevtech.cloud.storage.spring.model.UploadFileRequest;
import com.github.sevtech.cloud.storage.spring.model.UploadFileResponse;

import java.util.concurrent.Future;

public interface StorageService {

	UploadFileResponse uploadFile(UploadFileRequest request);

	Future<UploadFileResponse> uploadFileAsync(UploadFileRequest request);

	GetFileResponse getFile(GetFileRequest request);

	DeleteFileResponse deleteFile(DeleteFileRequest request);

}
