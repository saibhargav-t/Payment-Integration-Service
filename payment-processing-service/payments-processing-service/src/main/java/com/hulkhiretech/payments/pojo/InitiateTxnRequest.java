package com.hulkhiretech.payments.pojo;

import lombok.Data;

@Data
public class InitiateTxnRequest {

	private String firstName;
	private String lastName;
	private String email;

	private String country;
	private String locale;

	private String successUrl;
	private String failUrl;

}
