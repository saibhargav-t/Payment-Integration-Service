package com.hulkhiretech.payments.exceptions.handling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.hulkhiretech.payments.constants.ErrorEnum;
import com.hulkhiretech.payments.exceptions.ValidationException;
import com.hulkhiretech.payments.pojo.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GloabalExceptionHandler {

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
		log.error("Validation error occurred: {}", ex.getMessage(), ex);
		ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
		log.info("Returning error response: {}", errorResponse);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		// or any suitable status
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
		log.error("Validation error occurred: {}", ex.getMessage(), ex);
		ErrorResponse errorResponse = new ErrorResponse(ErrorEnum.GENERIC_ERROR.getErrorCode(),
				ErrorEnum.GENERIC_ERROR.getErrorMessage());
		log.info("Returning error response: {}", errorResponse);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		// or any suitable status
	}
}
