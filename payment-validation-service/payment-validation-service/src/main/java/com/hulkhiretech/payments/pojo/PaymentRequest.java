package com.hulkhiretech.payments.pojo;

import lombok.Data;

@Data
public class PaymentRequest {

	private long amount;
	private String currency;
	private String paymentMethod;
	private String paymentType;
	private String provider;
	private String customerID;
	private String mobileNo;
}