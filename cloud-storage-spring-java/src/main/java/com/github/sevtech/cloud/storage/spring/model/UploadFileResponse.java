package com.github.sevtech.cloud.storage.spring.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class UploadFileResponse extends BaseResponse {
	private String fileName;
	private String comment;
}