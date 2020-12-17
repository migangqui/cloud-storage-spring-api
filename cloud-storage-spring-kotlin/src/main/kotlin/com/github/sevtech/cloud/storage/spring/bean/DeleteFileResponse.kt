package com.github.sevtech.cloud.storage.spring.bean

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class DeleteFileResponse(var result: Boolean = false,
                              var status: Int = 0,
                              var cause: String? = null,
                              var exception: Exception? = null
) : BaseResponse(status, cause, exception)