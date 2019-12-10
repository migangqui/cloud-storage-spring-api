package com.github.sevtech.cloud.storage.spring.bean

import java.io.InputStream

class GetFileResponse(val content: InputStream? = null, val status: Int, cause: String? = null, val exception: Exception? = null)