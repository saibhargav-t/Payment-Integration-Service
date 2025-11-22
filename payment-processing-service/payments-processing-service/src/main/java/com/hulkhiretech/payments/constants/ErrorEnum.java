package com.hulkhiretech.payments.constants;

import lombok.Getter;

@Getter
public enum ErrorEnum {
	GENERIC_ERROR("20000", "Unable to process your request, please try later"),
	UNABLE_TO_CONNECT_TRUSTLY_PROVIDER("20001", "Unable to connect to Trustly Provider, please try later"),
	ERROR_PROCESSING_TRUSTLY_RESPONSE("20002", "Unable to connect to Trustly Provider, please try later");

	private final String errorCode;
	private final String errorMessage;

	ErrorEnum(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}