package com.github.sevtech.cloud.storage.spring.service.aws

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.*
import com.amazonaws.util.IOUtils
import com.github.sevtech.cloud.storage.spring.exception.NoBucketException
import com.github.sevtech.cloud.storage.spring.model.*
import com.github.sevtech.cloud.storage.spring.service.AbstractStorageService
import com.github.sevtech.cloud.storage.spring.service.StorageService
import org.apache.http.HttpStatus
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.util.StringUtils
import java.util.*
import java.util.concurrent.Future

class AwsS3Service(private val s3Client: AmazonS3) : AbstractStorageService(), StorageService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${amazon.s3.bucket.name}")
    private lateinit var defaultBucketName: String

    override fun uploadFile(request: UploadFileRequest): UploadFileResponse {
        return try {
            log.info("Uploading file to ${getFilePath(request)}")
            val bucketName = Optional.ofNullable(
                    Optional.ofNullable(request.bucketName).orElse(defaultBucketName))
                    .orElseThrow { NoBucketException("Bucket name not indicated") }

            val streamToUpload = request.stream.clone()

            val metadata = ObjectMetadata()
            metadata.contentLength = IOUtils.toByteArray(request.stream).size.toLong()

            if (StringUtils.hasText(request.contentType)) {
                metadata.contentType = request.contentType
                metadata.cacheControl = "s-maxage"
            }

            val uploadFileRequest = PutObjectRequest(bucketName, getFilePath(request), streamToUpload, metadata)
                    .withCannedAcl(request.accessControl)

            s3Client.putObject(uploadFileRequest)

            UploadFileResponse(fileName = request.name, status = HttpStatus.SC_OK)
        } catch (ase: AmazonServiceException) {
            showAmazonServiceExceptionUploadFileLogs(ase)
            UploadFileResponse(request.name, HttpStatus.SC_INTERNAL_SERVER_ERROR, ase.errorMessage, ase)
        } catch (ace: AmazonClientException) {
            showAmazonClientExceptionUploadFileLogs(ace)
            UploadFileResponse(request.name, HttpStatus.SC_INTERNAL_SERVER_ERROR, ace.message, ace)
        } catch (e: NoBucketException) {
            log.warn(e.message)
            UploadFileResponse(request.name, HttpStatus.SC_INTERNAL_SERVER_ERROR, e.message, e)
        } catch (e: Exception) {
            log.error(e.message)
            UploadFileResponse(request.name, HttpStatus.SC_INTERNAL_SERVER_ERROR, e.message, e)
        }
    }

    @Async
    override fun uploadFileAsync(request: UploadFileRequest): Future<UploadFileResponse> {
        return AsyncResult(uploadFile(request))
    }

    override fun getFile(request: GetFileRequest): GetFileResponse {
        log.info("Reading file from ${request.path}")
        val result: GetFileResponse = try {
            val s3Object: S3Object = s3Client.getObject(GetObjectRequest(getBucketName(request.bucketName, defaultBucketName), request.path))
            GetFileResponse(content = IOUtils.toByteArray(s3Object.objectContent), status = HttpStatus.SC_OK)
        } catch (e: NoBucketException) {
            log.error(e.message, e)
            GetFileResponse(cause = e.message, exception = e, status = HttpStatus.SC_INTERNAL_SERVER_ERROR)
        }
        return result
    }

    override fun deleteFile(request: DeleteFileRequest): DeleteFileResponse {
        log.info("Deleting file from $request.path")
        val result: DeleteFileResponse = try {
            val deleteObjectRequest = DeleteObjectRequest(getBucketName(request.bucketName, defaultBucketName), request.path)
            s3Client.deleteObject(deleteObjectRequest)
            DeleteFileResponse(result = true, status = HttpStatus.SC_OK)
        } catch (ase: AmazonServiceException) {
            showAmazonServiceExceptionUploadFileLogs(ase)
            DeleteFileResponse(cause = ase.message, exception = ase, status = HttpStatus.SC_INTERNAL_SERVER_ERROR)
        } catch (ace: AmazonClientException) {
            showAmazonClientExceptionUploadFileLogs(ace)
            DeleteFileResponse(cause = ace.message, exception = ace, status = HttpStatus.SC_INTERNAL_SERVER_ERROR)
        } catch (e: Exception) {
            log.error(e.message, e)
            DeleteFileResponse(cause = e.message, exception = e, status = HttpStatus.SC_INTERNAL_SERVER_ERROR)
        }
        return result
    }

    /* Private methods */

    private fun showAmazonServiceExceptionUploadFileLogs(ase: AmazonServiceException) {
        log.error(
                "Caught an AmazonServiceException, which means your request made it " +
                        "to Amazon S3, but was rejected with an error response for some reason."
        )
        log.error("Error Message:    ${ase.message}")
        log.error("HTTP Status Code: ${ase.statusCode}")
        log.error("AWS Error Code:   ${ase.errorCode}")
        log.error("Error Type:       ${ase.errorType}")
        log.error("Request ID:       ${ase.requestId}")
    }

    private fun showAmazonClientExceptionUploadFileLogs(ace: AmazonClientException) {
        log.error(
            "Caught an AmazonClientException, which means the client encountered " +
                    "an internal error while trying to communicate with S3, such as not being able to access the network.")

        log.error("Error Message: ${ace.message}")
    }
}