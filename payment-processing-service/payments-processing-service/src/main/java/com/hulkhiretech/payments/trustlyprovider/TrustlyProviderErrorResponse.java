package com.hulkhiretech.payments.trustlyprovider;

import lombok.Data;

@Data
public class TrustlyProviderErrorResponse {
    
	private String errorCode;
    private String errorMessage;

    public TrustlyProviderErrorResponse(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
