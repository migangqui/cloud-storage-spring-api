package com.github.sevtech.cloud.storage.spring.service.impl

import com.amazonaws.util.IOUtils
import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.models.BlobHttpHeaders
import com.github.sevtech.cloud.storage.spring.bean.*
import com.github.sevtech.cloud.storage.spring.exception.NoBucketException
import com.github.sevtech.cloud.storage.spring.service.AbstractStorageService
import com.github.sevtech.cloud.storage.spring.service.StorageService
import mu.KotlinLogging
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.AsyncResult
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.Future

class AzureBlobStorageService(private val blobServiceClient: BlobServiceClient) : AbstractStorageService(), StorageService {

    private val log = KotlinLogging.logger {}

    @Value("\${azure.blob.storage.container.name}")
    private lateinit var defaultBucketName: String

    override fun uploadFile(request: UploadFileRequest): UploadFileResponse {
        var result: UploadFileResponse
        try {
            val bucketName = getBucketName(request.bucketName, defaultBucketName)
            val streamToUpload: InputStream? = request.stream.clone()
            val blockBlobClient = blobServiceClient.getBlobContainerClient(bucketName).getBlobClient(getFilePath(request)).blockBlobClient
            val headers = BlobHttpHeaders()
            headers.contentType = request.contentType
            blockBlobClient.upload(streamToUpload, IOUtils.toByteArray(request.stream).size.toLong())
            blockBlobClient.setHttpHeaders(headers)
            result = UploadFileResponse(fileName = request.name, status = HttpStatus.SC_OK, comment = blockBlobClient.blobUrl)
        } catch (e: IOException) {
            log.warn("Error creating blob")
            result = UploadFileResponse(fileName = request.name, status = HttpStatus.SC_INTERNAL_SERVER_ERROR, cause = "Error creating blob", exception = e)
        } catch (e: NoBucketException) {
            log.warn("Error creating blob")
            result = UploadFileResponse(fileName = request.name, status = HttpStatus.SC_INTERNAL_SERVER_ERROR, cause = "Error creating blob", exception = e)
        }
        return result
    }

    override fun uploadFileAsync(request: UploadFileRequest): Future<UploadFileResponse> {
        return AsyncResult(uploadFile(request))
    }

    override fun getFile(request: GetFileRequest): GetFileResponse {
        TODO()
    }

    override fun deleteFile(request: DeleteFileRequest): DeleteFileResponse {
        TODO()
    }

}