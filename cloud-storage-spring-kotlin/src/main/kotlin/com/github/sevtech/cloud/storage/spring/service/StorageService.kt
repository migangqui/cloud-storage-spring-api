package com.github.sevtech.cloud.storage.spring.service

import com.github.sevtech.cloud.storage.spring.model.*
import java.util.concurrent.Future

interface StorageService {
    fun uploadFile(request: UploadFileRequest): UploadFileResponse

    fun uploadFileAsync(request: UploadFileRequest): Future<UploadFileResponse>

    fun getFile(request: GetFileRequest): GetFileResponse

    fun deleteFile(request: DeleteFileRequest): DeleteFileResponse
}