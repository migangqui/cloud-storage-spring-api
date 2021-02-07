package com.github.sevtech.cloud.storage.spring.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BaseResponse {
    private int status;
    private String cause;
    private Exception exception;
}
