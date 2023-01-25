package com.github.sevtech.localstack.kotlin.example

import com.github.sevtech.cloud.storage.spring.model.DeleteFileRequest
import com.github.sevtech.cloud.storage.spring.model.GetFileRequest
import com.github.sevtech.cloud.storage.spring.model.UploadFileRequest
import com.github.sevtech.cloud.storage.spring.model.UploadFileResponse
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
class MediaController(private val storageService: StorageService) {


    @PostMapping("/files")
    @Throws(IOException::class)
    fun uploadFile(@RequestBody file: MultipartFile, @RequestParam name: String, @RequestParam folder: String): UploadFileResponse {
        return storageService.uploadFile(UploadFileRequest(stream = ByteArrayInputStream(file.bytes), folder = folder, name = name, contentType = file.contentType!!))
    }

    @GetMapping("/files")
    @Throws(IOException::class)
    fun getFile(@RequestParam name: String): ByteArray? {
        return storageService.getFile(GetFileRequest(path = name)).content
    }

    @DeleteMapping("/files")
    @Throws(IOException::class)
    fun deleteFile(@RequestParam name: String): Int {
        return storageService.deleteFile(DeleteFileRequest(path = name)).status
    }

}


