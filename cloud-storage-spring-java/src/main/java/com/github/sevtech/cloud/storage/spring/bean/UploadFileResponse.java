package com.github.sevtech.cloud.storage.spring.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UploadFileResponse {
	private String fileName;
	private int status;
	private String cause;
	private Exception exception;
	private String comment;
}
