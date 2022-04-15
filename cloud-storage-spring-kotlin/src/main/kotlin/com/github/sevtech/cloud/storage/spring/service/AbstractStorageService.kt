package com.github.sevtech.cloud.storage.spring.service

import com.github.sevtech.cloud.storage.spring.model.UploadFileRequest
import com.github.sevtech.cloud.storage.spring.exception.NoBucketException
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*

abstract class AbstractStorageService {

    protected fun InputStream.clone(): InputStream? {
        var result: InputStream? = null
        try {
            this.mark(0)
            val outputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var readLength = this.read(buffer)
            while ((readLength) != -1) {
                outputStream.write(buffer, 0, readLength)
                readLength = this.read(buffer)
            }
            this.reset()
            outputStream.flush()
            result = ByteArrayInputStream(outputStream.toByteArray())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return result
    }

    @Throws(NoBucketException::class)
    protected fun getBucketName(bucketName: String?, defaultBucketName: String): String {
        return Optional.ofNullable(
                Optional.ofNullable(bucketName).orElse(defaultBucketName))
                .orElseThrow { NoBucketException("Bucket name not indicated") }
    }

    protected fun getFilePath(request: UploadFileRequest): String {
        return request.folder + "/" + request.name
    }
}