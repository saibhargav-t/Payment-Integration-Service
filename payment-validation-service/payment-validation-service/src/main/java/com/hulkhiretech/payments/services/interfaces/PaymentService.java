package com.hulkhiretech.payments.services.interfaces;

import com.hulkhiretech.payments.pojo.PaymentRequest;
import com.hulkhiretech.payments.pojo.PaymentResponse;

public interface PaymentService {
	
	public PaymentResponse createPayment(PaymentRequest paymentDetails);

}

