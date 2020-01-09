package com.github.sevtech.cloud.storage.spring.bean

class GetFileResponse(val content:ByteArray? = null, val status: Int, cause: String? = null, val exception: Exception? = null)