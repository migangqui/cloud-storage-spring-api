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
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.Future

class AzureBlobStorageService(private val blobServiceClient: BlobServiceClient) : AbstractStorageService(), StorageService {

    private val log = KotlinLogging.logger {}

    @Value("\${azure.blob.storage.container.name}")
    private lateinit var defaultContainerName: String

    override fun uploadFile(request: UploadFileRequest): UploadFileResponse {
        return try {
            val bucketName = getBucketName(request.bucketName, defaultContainerName)
            val streamToUpload: InputStream? = request.stream.clone()
            val blockBlobClient = blobServiceClient.getBlobContainerClient(bucketName).getBlobClient(getFilePath(request)).blockBlobClient
            val headers = BlobHttpHeaders()
            headers.contentType = request.contentType
            blockBlobClient.upload(streamToUpload, IOUtils.toByteArray(request.stream).size.toLong())
            blockBlobClient.setHttpHeaders(headers)
            UploadFileResponse(fileName = request.name, status = HttpStatus.SC_OK, comment = blockBlobClient.blobUrl)
        } catch (e: IOException) {
            log.warn("Error creating blob")
            UploadFileResponse(fileName = request.name, status = HttpStatus.SC_INTERNAL_SERVER_ERROR, cause = "Error creating blob", exception = e)
        } catch (e: NoBucketException) {
            log.warn("Error creating blob")
            UploadFileResponse(fileName = request.name, status = HttpStatus.SC_INTERNAL_SERVER_ERROR, cause = "Error creating blob", exception = e)
        }
    }

    override fun uploadFileAsync(request: UploadFileRequest): Future<UploadFileResponse> {
        return AsyncResult(uploadFile(request))
    }

    override fun getFile(request: GetFileRequest): GetFileResponse {
        log.info("Reading file from Azure {}", request.path)
        return try {
            val outputStream = ByteArrayOutputStream()
            val bucketName = getBucketName(request.bucketName, defaultContainerName)
            val blockBlobClient = blobServiceClient.getBlobContainerClient(bucketName).getBlobClient(request.path).blockBlobClient
            blockBlobClient.download(outputStream)
            GetFileResponse(content = outputStream.toByteArray(), status = HttpStatus.SC_OK)
        } catch (e: IOException) {
            log.error(e.message, e)
            GetFileResponse(cause = e.message, exception = e, status = HttpStatus.SC_INTERNAL_SERVER_ERROR)
        } catch (e: NoBucketException) {
            log.error(e.message, e)
            GetFileResponse(cause = e.message, exception = e, status = HttpStatus.SC_INTERNAL_SERVER_ERROR)
        }
    }

    override fun deleteFile(request: DeleteFileRequest): DeleteFileResponse {
        TODO()
    }

}