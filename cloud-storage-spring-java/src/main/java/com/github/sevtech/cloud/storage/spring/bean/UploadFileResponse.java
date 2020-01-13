package com.github.sevtech.cloud.storage.spring.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UploadFileResponse {
	private String fileName;
	private int status;
	private String cause;
	private Exception exception;
	private String comment;
}