package com.hulkhiretech.payments.entity;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Transaction {

	private int id;
	private int userId;

	private int paymentMethodId;
	private int providerId;
	private int paymentTypeId;
	private int txnStatusId;

	private BigDecimal amount;
	private String currency;

	private String errorCode;
	private String errorMessage;

	private String merchantTransactionReference;
	private String txnReference;
	private String providerReference;
	private int retryCount;
}
