package com.hulkhiretech.payments.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TransactionDto {

	private int id;
	private int userId;

	private String paymentMethod;
	private String provider;
	private String paymentType;

	private String txnStatus;

	private BigDecimal amount;
	private String currency;

	private String errorCode;
	private String errorMessage;

	private String merchantTransactionReference;
	private String txnReference;
	private String providerReference;
	private int retryCount;
}
