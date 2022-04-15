package com.github.sevtech.cloud.storage.spring.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
open class BaseResponse(status: Int, cause: String?, exception: Exception?)