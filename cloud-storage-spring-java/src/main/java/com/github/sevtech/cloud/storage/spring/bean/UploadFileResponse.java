package com.github.sevtech.cloud.storage.spring.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UploadFileResponse extends BaseResponse {
	private String fileName;
	private String comment;
}