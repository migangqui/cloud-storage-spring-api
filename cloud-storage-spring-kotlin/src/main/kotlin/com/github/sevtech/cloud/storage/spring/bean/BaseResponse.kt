package com.github.sevtech.cloud.storage.spring.bean

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
open class BaseResponse(status: Int, cause: String?, exception: Exception?)