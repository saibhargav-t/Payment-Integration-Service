package com.hulkhiretech.payments.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hulkhiretech.payments.pojo.CreateTransaction;
import com.hulkhiretech.payments.pojo.CreateTransactionResponse;
import com.hulkhiretech.payments.pojo.InitiateTxnRequest;
import com.hulkhiretech.payments.pojo.PaymentResponse;
import com.hulkhiretech.payments.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/payments")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping
	public CreateTransactionResponse createPayment(@RequestBody CreateTransaction transaction) {
		log.info("Payment Request Received  in Payments Controller");
		log.info("Received payment request| createTxn:{}", transaction);
		CreateTransactionResponse response = paymentService.createPayment(transaction);
		log.info("Transaction details: {}", response);
		// Created
		return response;
	}

	@PostMapping("/{transactionReference}/initiate")
	public PaymentResponse initiatePayment(@PathVariable String transactionReference,
			@RequestBody InitiateTxnRequest request) {
		log.info("Initiating payment for txnReference: {}, initiateTxnRequest: {}", transactionReference, request);
		PaymentResponse response = paymentService.initiatePayment(transactionReference, request);
		log.info("Payment initiation response: {}", response);

		return response;
	}
}
