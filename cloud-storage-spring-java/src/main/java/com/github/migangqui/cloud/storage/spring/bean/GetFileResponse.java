package com.github.migangqui.cloud.storage.spring.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

@Getter
@Setter
@Builder
public class GetFileResponse {
	private InputStream content;
	private int status;
	private String cause;
	private Exception exception;
}
