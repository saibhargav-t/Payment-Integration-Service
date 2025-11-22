package com.hulkhiretech.payments.pojo;

import lombok.Data;

@Data
public class PaymentResponse {

	private String txnReference;

	private String txnStatus;

	private String url;

}