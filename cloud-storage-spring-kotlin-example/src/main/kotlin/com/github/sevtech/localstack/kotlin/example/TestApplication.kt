package com.github.sevtech.localstack.kotlin.example

import com.github.sevtech.cloud.storage.spring.bean.UploadFileRequest
import com.github.sevtech.cloud.storage.spring.bean.UploadFileResponse
import com.github.sevtech.cloud.storage.spring.service.StorageService
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
class MediaController(private val azureBlobStorageService: StorageService) {

    /* Azure*/
    @PostMapping("/azure/files")
    @Throws(IOException::class)
    fun uploadFileAzure(@RequestBody file: MultipartFile, @RequestParam name: String, @RequestParam folder: String): UploadFileResponse {
        return azureBlobStorageService.uploadFile(UploadFileRequest(stream = ByteArrayInputStream(file.bytes), folder = folder, name = name, contentType = file.contentType!!))
    }
}


