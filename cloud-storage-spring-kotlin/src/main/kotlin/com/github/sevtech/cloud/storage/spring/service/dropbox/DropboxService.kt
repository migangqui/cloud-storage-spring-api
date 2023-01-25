package com.github.sevtech.cloud.storage.spring.service.dropbox

import com.dropbox.core.DbxException
import com.dropbox.core.v2.DbxClientV2
import com.github.sevtech.cloud.storage.spring.model.*
import com.github.sevtech.cloud.storage.spring.service.AbstractStorageService
import com.github.sevtech.cloud.storage.spring.service.StorageService
import org.apache.http.HttpStatus
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.AsyncResult
import java.io.IOException
import java.util.concurrent.Future

class DropboxService(private val dbxClientV2: DbxClientV2) : AbstractStorageService(), StorageService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun uploadFile(request: UploadFileRequest): UploadFileResponse {
        return try {
            log.info("Uploading file to ${getFilePath(request)}")
            dbxClientV2.files().uploadBuilder("/" + getFilePath(request))
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
            log.info("Reading file from {}", request.path)
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
            log.info("Deleting file from {}", request.path)
            dbxClientV2.files().deleteV2(request.path)
            DeleteFileResponse(true, HttpStatus.SC_OK)
        } catch (e: DbxException) {
            DeleteFileResponse(status = HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    cause = e.localizedMessage, exception = e)
        }
    }

}