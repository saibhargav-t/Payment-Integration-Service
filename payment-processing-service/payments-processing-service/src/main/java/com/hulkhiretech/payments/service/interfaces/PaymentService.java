package com.hulkhiretech.payments.service.interfaces;

import com.hulkhiretech.payments.pojo.CreateTransaction;
import com.hulkhiretech.payments.pojo.CreateTransactionResponse;
import com.hulkhiretech.payments.pojo.InitiateTxnRequest;
import com.hulkhiretech.payments.pojo.PaymentResponse;

public interface PaymentService {

	public CreateTransactionResponse createPayment(CreateTransaction createTransaction);

	public PaymentResponse initiatePayment(String txnReference, InitiateTxnRequest initiateTxnRequest);

}
