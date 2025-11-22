package com.hulkhiretech.payments.trustlyprovider;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class TrustlyProviderDepositResponse {
	@SerializedName("orderId")
	private String orderid;
	@SerializedName("uri")
	private String url;

}
