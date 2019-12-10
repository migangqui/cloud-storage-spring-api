package com.github.sevtech.localstack.kotlin.example

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@SpringBootApplication
class TestApplication

fun main(args: Array<String>) {
    SpringApplication.run(TestApplication::class.java, *args)
}

//@RestController
//@RequestMapping("/api/files")
//class MediaController(private val amazonS3Service: AmazonS3Service) {
//    @PostMapping
//    @Throws(IOException::class)
//    fun uploadFile(@RequestBody file: MultipartFile, @RequestParam folder: String, @RequestParam name: String): UploadFileResponse {
//        return amazonS3Service.uploadFile(UploadFileRequest(ByteArrayInputStream(file.bytes), folder, name, file.contentType!!))
//    }
//}


