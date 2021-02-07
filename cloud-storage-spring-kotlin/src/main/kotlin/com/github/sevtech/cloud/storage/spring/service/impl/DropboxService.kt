package com.github.sevtech.cloud.storage.spring.service.impl

import com.dropbox.core.DbxException
import com.dropbox.core.v2.DbxClientV2
import com.github.sevtech.cloud.storage.spring.bean.*
import com.github.sevtech.cloud.storage.spring.service.StorageService
import mu.KotlinLogging
import org.apache.http.HttpStatus
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.AsyncResult
import java.io.IOException
import java.util.concurrent.Future

class DropboxService(private val dbxClientV2: DbxClientV2) : StorageService {

    private val log = KotlinLogging.logger {}

    override fun uploadFile(request: UploadFileRequest): UploadFileResponse {
        return try {
            dbxClientV2.files().uploadBuilder("/" + request.folder + "/" + request.name)
                    .uploadAndFinish(request.stream)
            UploadFileResponse(request.name, HttpStatus.SC_OK)
        } catch (e: DbxException) {
            UploadFileResponse(fileName = request.name, status = HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    cause = e.localizedMessage, exception = e)
        } catch (e: IOException) {
            UploadFileResponse(fileName = request.name, status = HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    cause = e.localizedMessage, exception = e)
        }
    }

    @Async
    override fun uploadFileAsync(request: UploadFileRequest): Future<UploadFileResponse> {
        return AsyncResult(uploadFile(request))
    }

    override fun getFile(request: GetFileRequest): GetFileResponse {
        return try {
            val download = dbxClientV2.files().download(request.path)
            GetFileResponse(ByteArray(download.inputStream.available()), HttpStatus.SC_OK)
        } catch (e: DbxException) {
            GetFileResponse(status = HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    cause = e.localizedMessage, exception = e)
        } catch (e: IOException) {
            GetFileResponse(status = HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    cause = e.localizedMessage, exception = e)
        }
    }

    override fun deleteFile(request: DeleteFileRequest): DeleteFileResponse {
        return try {
            dbxClientV2.files().deleteV2(request.path)
            DeleteFileResponse(true, HttpStatus.SC_OK)
        } catch (e: DbxException) {
            DeleteFileResponse(status = HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    cause = e.localizedMessage, exception = e)
        }
    }

}