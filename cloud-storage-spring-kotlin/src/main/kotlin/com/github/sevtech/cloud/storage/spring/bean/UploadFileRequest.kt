package com.github.sevtech.cloud.storage.spring.bean

import com.amazonaws.services.s3.model.CannedAccessControlList
import java.io.InputStream

class UploadFileRequest(var stream: InputStream,
                        var folder: String,
                        var name: String,
                        var contentType: String,
                        var bucketName: String? = null,
                        var accessControl: CannedAccessControlList = CannedAccessControlList.Private)