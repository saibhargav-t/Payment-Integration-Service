package com.hulkhiretech.payments.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.hulkhiretech.payments.constants.ErrorEnum;
import com.hulkhiretech.payments.dto.TransactionDto;
import com.hulkhiretech.payments.exception.TrustlyProviderException;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.pojo.InitiateTxnRequest;
import com.hulkhiretech.payments.trustlyprovider.TrustlyProviderDepositRequest;
import com.hulkhiretech.payments.trustlyprovider.TrustlyProviderDepositResponse;
import com.hulkhiretech.payments.trustlyprovider.TrustlyProviderErrorResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceHelper {

	private final Gson gson;

	@Value("${trustlyprovider.deposit.url}")
	private String trustlyProviderDepositUrl;

	public TrustlyProviderDepositResponse processResponse(
			ResponseEntity<String> httpResponse) {
		log.info("Processing HTTP response: {}", httpResponse);

		if(httpResponse.getStatusCode().is2xxSuccessful()) {// Success

			String body = httpResponse.getBody();
			log.info("HTTP response body: {}", body);

			TrustlyProviderDepositResponse depositResponse = gson.fromJson(
					body, TrustlyProviderDepositResponse.class);
			log.info("Parsed TrustlyDepositResponse: {}", depositResponse);

			if (depositResponse != null
					&& depositResponse.getUrl() != null) {
				log.error("Deposit response or its data is null");

				log.info("DepositResponse created: {}", depositResponse);

				return depositResponse;
			}

			log.error("Deposit response is invalid");
		}

		// Else failure
		// Valid Error Response
		if(httpResponse.getStatusCode().is4xxClientError() || 
				httpResponse.getStatusCode().is5xxServerError()) {
			log.error("HTTP error occurred: {}", httpResponse.getBody());

			TrustlyProviderErrorResponse errorResponse = gson.fromJson(
					httpResponse.getBody(), TrustlyProviderErrorResponse.class);

			log.error("Parsed TrustlyErrorResponse: {}", errorResponse);

			if (errorResponse != null) {
				log.error("Error response or its error field is null");

				throw new TrustlyProviderException(
						errorResponse.getErrorCode(),
						errorResponse.getErrorMessage(), 
						HttpStatus.valueOf(httpResponse.getStatusCode().value()));
			}

			log.error("Error response is invalid: {}", errorResponse);
		}

		log.error("Unexpected response from Trustly: {}", httpResponse.getBody());

		throw new TrustlyProviderException(
				ErrorEnum.ERROR_PROCESSING_TRUSTLY_RESPONSE.getErrorCode(), 
				ErrorEnum.ERROR_PROCESSING_TRUSTLY_RESPONSE.getErrorMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR);

	}

	public HttpRequest prepareInitiateRequest(TransactionDto txnDTO, 
			InitiateTxnRequest initiateTxnRequest) {

		log.info("Preparing initiate request for txnDTO: {}, initiateTxnRequest: {}", 
				txnDTO, initiateTxnRequest);

		TrustlyProviderDepositRequest request = TrustlyProviderDepositRequest.builder()
				.txnReference(txnDTO.getTxnReference())
				.endUserId(String.valueOf(txnDTO.getUserId()))
				.amount(txnDTO.getAmount().doubleValue())
				.currency(txnDTO.getCurrency())
				.firstName(initiateTxnRequest.getFirstName())
				.lastName(initiateTxnRequest.getLastName())
				.email(initiateTxnRequest.getEmail())
				.country(initiateTxnRequest.getCountry())
				.locale(initiateTxnRequest.getLocale())
				.successUrl(initiateTxnRequest.getSuccessUrl())
				.failUrl(initiateTxnRequest.getFailUrl())
				.build();

		String jsonReqData = gson.toJson(request);
		log.info("JSON request data: {}", jsonReqData);

		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setBody(jsonReqData);
		httpRequest.setHttpMethod(HttpMethod.POST);

		httpRequest.setUrl(trustlyProviderDepositUrl);

		log.info("Prepared HttpRequest: {}", httpRequest);

		return httpRequest;
	}
}
