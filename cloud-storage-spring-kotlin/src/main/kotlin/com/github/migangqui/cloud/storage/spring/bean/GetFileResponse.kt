package com.github.migangqui.cloud.storage.spring.bean

import java.io.InputStream
import java.lang.Exception

class GetFileResponse(val content: InputStream? = null, val status: Int, cause: String? = null, val exception: Exception? = null)