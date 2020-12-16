package com.github.sevtech.localstack.kotlin.example

import com.github.sevtech.cloud.storage.spring.bean.DeleteFileRequest
import com.github.sevtech.cloud.storage.spring.bean.GetFileRequest
import com.github.sevtech.cloud.storage.spring.bean.UploadFileRequest
import com.github.sevtech.cloud.storage.spring.bean.UploadFileResponse
import com.github.sevtech.cloud.storage.spring.service.StorageService
import com.github.sevtech.cloud.storage.spring.service.impl.DropboxService
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.IOException

@EnableAsync
@SpringBootApplication
class TestApplication

fun main(args: Array<String>) {
    SpringApplication.run(TestApplication::class.java, *args)
}

@RestController
@RequestMapping("/")
class MediaController(private val storageService: StorageService) {


    @PostMapping("/files")
    @Throws(IOException::class)
    fun uploadFileAzure(@RequestBody file: MultipartFile, @RequestParam name: String, @RequestParam folder: String): UploadFileResponse {
        return storageService.uploadFile(UploadFileRequest(stream = ByteArrayInputStream(file.bytes), folder = folder, name = name, contentType = file.contentType!!))
    }

    @GetMapping("/files")
    @Throws(IOException::class)
    fun getFileAzure(@RequestParam name: String): ByteArray? {
        return storageService.getFile(GetFileRequest(path = name)).content
    }

    @DeleteMapping("/files")
    @Throws(IOException::class)
    fun deleteFileAzure(@RequestParam name: String): Int {
        return storageService.deleteFile(DeleteFileRequest(path = name)).status
    }

}


