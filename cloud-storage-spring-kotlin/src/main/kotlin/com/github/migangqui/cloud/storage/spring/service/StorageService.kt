package com.github.migangqui.cloud.storage.spring.service

import com.github.migangqui.cloud.storage.spring.bean.*
import java.util.concurrent.Future

interface StorageService {
    fun uploadFile(request: UploadFileRequest): UploadFileResponse

    fun uploadFileAsync(request: UploadFileRequest): Future<UploadFileResponse>

    fun getFile(request: GetFileRequest): GetFileResponse

    fun deleteFile(request: DeleteFileRequest): DeleteFileResponse
}