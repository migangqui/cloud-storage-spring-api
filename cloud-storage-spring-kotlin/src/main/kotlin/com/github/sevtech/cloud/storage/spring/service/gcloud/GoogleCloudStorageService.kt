package com.github.sevtech.cloud.storage.spring.service.gcloud

import com.amazonaws.util.IOUtils
import com.github.sevtech.cloud.storage.spring.exception.NoBucketException
import com.github.sevtech.cloud.storage.spring.model.*
import com.github.sevtech.cloud.storage.spring.service.AbstractStorageService
import com.github.sevtech.cloud.storage.spring.service.StorageService
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import org.apache.http.HttpStatus
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.AsyncResult
import java.io.IOException
import java.util.*
import java.util.concurrent.Future

class GoogleCloudStorageService(private val storageClient: Storage) : AbstractStorageService(), StorageService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${gcp.storage.bucket.name}")
    private lateinit var defaultBucketName: String

    override fun uploadFile(request: UploadFileRequest): UploadFileResponse {
        return try {
            log.info("Uploading file to ${getFilePath(request)}")
            val bucketName = Optional.ofNullable(
                Optional.ofNullable<String?>(request.bucketName).orElse(
                    defaultBucketName
                )
            )
                .orElseThrow { NoBucketException("Bucket name not indicated") }
            val blobInfo: BlobInfo = storageClient.create(
                BlobInfo.newBuilder(bucketName, getFilePath(request)).build(), IOUtils.toByteArray(request.stream)
                // setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
            )
            UploadFileResponse(
                    fileName = request.name,
                    status = HttpStatus.SC_OK,
                    comment = blobInfo.mediaLink
            )
        } catch (e: NoBucketException) {
            log.warn("Error creating blob")
            UploadFileResponse(fileName = request.name, status = HttpStatus.SC_OK, cause = "Error creating blob",
                    exception = e)
        } catch (e: IOException) {
            log.warn("Error creating blob")
            UploadFileResponse(fileName = request.name, status = HttpStatus.SC_OK, cause = "Error creating blob",
                    exception = e)
        }
    }

    @Async
    override fun uploadFileAsync(request: UploadFileRequest): Future<UploadFileResponse> {
        return AsyncResult(
            uploadFile(request)
        )
    }

    override fun getFile(request: GetFileRequest): GetFileResponse {
        log.info("Reading file from ${request.path}")
        return try {
            val file = storageClient.readAllBytes(BlobId.of(getBucketName(request.bucketName), request.path))
            GetFileResponse(content = file, status = HttpStatus.SC_OK)
        } catch (e: NoBucketException) {
            log.error(e.message, e)
            GetFileResponse(cause = e.message, exception = e, status = HttpStatus.SC_INTERNAL_SERVER_ERROR)
        }
    }

    override fun deleteFile(request: DeleteFileRequest): DeleteFileResponse {
        log.info("Deleting file from $request.path")
        return try {
            storageClient.delete(BlobId.of(getBucketName(request.bucketName), request.path))
            DeleteFileResponse(result = true, status = HttpStatus.SC_OK)
        } catch (e: NoBucketException) {
            log.error(e.message, e)
            DeleteFileResponse(cause = e.message, exception = e, status = HttpStatus.SC_INTERNAL_SERVER_ERROR)
        }
    }

    @Throws(NoBucketException::class)
    private fun getBucketName(bucketName: String?): String? {
        return Optional.ofNullable(
                Optional.ofNullable(bucketName).orElse(defaultBucketName))
                .orElseThrow { NoBucketException("Bucket name not indicated") }
    }
}