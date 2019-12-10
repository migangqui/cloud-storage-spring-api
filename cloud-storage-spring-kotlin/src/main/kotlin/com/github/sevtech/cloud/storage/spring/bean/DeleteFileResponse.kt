package com.github.sevtech.cloud.storage.spring.bean

class DeleteFileResponse(val result: Boolean = false, val status: Int, cause: String? = null, val exception: Exception? = null)