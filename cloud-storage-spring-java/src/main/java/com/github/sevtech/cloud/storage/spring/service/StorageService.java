package com.github.sevtech.cloud.storage.spring.service;

import com.github.sevtech.cloud.storage.spring.bean.DeleteFileRequest;
import com.github.sevtech.cloud.storage.spring.bean.DeleteFileResponse;
import com.github.sevtech.cloud.storage.spring.bean.GetFileRequest;
import com.github.sevtech.cloud.storage.spring.bean.GetFileResponse;
import com.github.sevtech.cloud.storage.spring.bean.UploadFileRequest;
import com.github.sevtech.cloud.storage.spring.bean.UploadFileResponse;

import java.util.concurrent.Future;

public interface StorageService {

	UploadFileResponse uploadFile(UploadFileRequest request);

	Future<UploadFileResponse> uploadFileAsync(UploadFileRequest request);

	GetFileResponse getFile(GetFileRequest request);

	DeleteFileResponse deleteFile(DeleteFileRequest request);

}
