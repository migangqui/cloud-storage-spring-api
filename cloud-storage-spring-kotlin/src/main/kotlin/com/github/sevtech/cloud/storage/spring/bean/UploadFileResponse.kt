package com.github.sevtech.cloud.storage.spring.bean

data class UploadFileResponse(
    var fileName: String? = null,
    var status: Int = 0,
    var cause: String? = null,
    var exception: Exception? = null,
    var comment : String? = null
)