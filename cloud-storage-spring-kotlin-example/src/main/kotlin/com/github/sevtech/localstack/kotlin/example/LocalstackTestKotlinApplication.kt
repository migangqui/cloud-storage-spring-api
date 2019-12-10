package com.github.sevtech.localstack.kotlin.example

import com.github.migangqui.spring.aws.s3.bean.UploadFileRequest
import com.github.migangqui.spring.aws.s3.bean.UploadFileResponse
import com.github.migangqui.spring.aws.s3.service.AmazonS3Service
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.IOException

@EnableAsync
@SpringBootApplication
class LocalstackTestKotlinApplication

fun main(args: Array<String>) {
    SpringApplication.run(LocalstackTestKotlinApplication::class.java, *args)
}

@RestController
@RequestMapping("/api/files")
class MediaController(private val amazonS3Service: AmazonS3Service) {
    @PostMapping
    @Throws(IOException::class)
    fun uploadFile(@RequestBody file: MultipartFile, @RequestParam folder: String, @RequestParam name: String): UploadFileResponse {
        return amazonS3Service.uploadFile(UploadFileRequest(ByteArrayInputStream(file.bytes), folder, name, file.contentType!!))
    }
}


