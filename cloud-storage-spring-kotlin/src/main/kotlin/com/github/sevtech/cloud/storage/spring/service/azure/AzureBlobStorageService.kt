package com.github.sevtech.cloud.storage.spring.service.azure

import com.amazonaws.util.IOUtils
import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.models.BlobHttpHeaders
import com.azure.storage.blob.specialized.BlockBlobClient
import com.github.sevtech.cloud.storage.spring.exception.NoBucketException
import com.github.sevtech.cloud.storage.spring.model.*
import com.github.sevtech.cloud.storage.spring.service.AbstractStorageService
import com.github.sevtech.cloud.storage.spring.service.StorageService
import org.apache.http.HttpStatus
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.AsyncResult
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.Future

class AzureBlobStorageService(private val blobServiceClient: BlobServiceClient) : AbstractStorageService(), StorageService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${azure.blob.storage.container.name}")
    private lateinit var defaultContainerName: String

    override fun uploadFile(request: UploadFileRequest): UploadFileResponse {
        return try {
            log.info("Uploading file to ${getFilePath(request)}")
            val streamToUpload: InputStream? = request.stream.clone()
            val blockBlobClient = getBlobClient(request.bucketName, getFilePath(request))
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
        log.info("Reading file from {}", request.path)
        return try {
            val outputStream = ByteArrayOutputStream()
            val blockBlobClient = getBlobClient(request.bucketName, request.path)
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
        log.info("Deleting file from {}", request.path)
        return try {
            val blockBlobClient: BlockBlobClient = getBlobClient(request.bucketName, request.path)
            blockBlobClient.delete()
            DeleteFileResponse(status = HttpStatus.SC_OK)
        } catch (e: NoBucketException) {
            log.error(e.message, e)
            DeleteFileResponse(cause = e.message, exception = e, status = HttpStatus.SC_INTERNAL_SERVER_ERROR)
        }
    }

    @Throws(NoBucketException::class)
    private fun getBlobClient(bucketName: String?, path: String): BlockBlobClient {
        return blobServiceClient.getBlobContainerClient(getBucketName(bucketName, defaultContainerName)).getBlobClient(path).blockBlobClient
    }

}