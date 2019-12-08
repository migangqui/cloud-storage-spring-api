package com.github.migangqui.cloud.storage.spring.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DeleteFileResponse {
	private boolean result;
	private int status;
	private String cause;
	private Exception exception;
}
