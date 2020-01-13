package com.github.sevtech.cloud.storage.spring.java.example;

import com.github.sevtech.cloud.storage.spring.bean.DeleteFileRequest;
import com.github.sevtech.cloud.storage.spring.bean.GetFileRequest;
import com.github.sevtech.cloud.storage.spring.bean.UploadFileRequest;
import com.github.sevtech.cloud.storage.spring.bean.UploadFileResponse;
import com.github.sevtech.cloud.storage.spring.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping
@EnableAsync
@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

//    @Autowired
//    private StorageService awsS3Service;
//    @Autowired
//    private StorageService googleCloudStorageService;
    @Autowired
    private StorageService azureBlobStorageService;

    /* AWS */

//    @PostMapping("/aws/files")
//    public UploadFileResponse uploadFileAws(@RequestBody MultipartFile file, @RequestParam String folder, @RequestParam String name) throws IOException {
//        return awsS3Service.uploadFile(UploadFileRequest.builder().stream(new ByteArrayInputStream(file.getBytes())).folder(folder).name(name).contentType(file.getContentType()).build());
//    }
//
//    /* Google Cloud */
//
//    @PostMapping("/gcp/files")
//    public UploadFileResponse uploadFileGcp(@RequestBody MultipartFile file, @RequestParam String folder, @RequestParam String name) throws IOException {
//        return googleCloudStorageService.uploadFile(UploadFileRequest.builder().stream(new ByteArrayInputStream(file.getBytes())).folder(folder).name(name).contentType(file.getContentType()).build());
//    }

    /* Azure*/

    @PostMapping("/azure/files")
    public UploadFileResponse uploadFileAzure(@RequestBody MultipartFile file, @RequestParam String name, @RequestParam String folder) throws IOException {
        return azureBlobStorageService.uploadFile(UploadFileRequest.builder().stream(new ByteArrayInputStream(file.getBytes())).folder(folder).name(name).contentType(file.getContentType()).build());
    }

    @GetMapping("/azure/files")
    public byte[] getFileAzure(@RequestParam String name) throws IOException {
        return azureBlobStorageService.getFile(GetFileRequest.builder().path(name).build()).getContent();
    }

    @DeleteMapping("/azure/files")
    public int deleteFileAzure(@RequestParam String name) throws IOException {
        return azureBlobStorageService.deleteFile(DeleteFileRequest.builder().path(name).build()).getStatus();
    }
}
