package com.hulkhiretech.payments.trustlyprovider;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrustlyProviderDepositRequest {

	private String txnReference;
	private String endUserId;
	
	private Double amount;
	private String currency;

	private String firstName;
	private String lastName;
	private String email;
	
	private String country;
	private String locale;
	
	private String successUrl;
	private String failUrl;
	
}
