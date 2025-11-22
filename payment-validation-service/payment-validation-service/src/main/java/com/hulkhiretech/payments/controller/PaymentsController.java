package com.hulkhiretech.payments.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hulkhiretech.payments.constants.Constants;
import com.hulkhiretech.payments.pojo.PaymentRequest;
import com.hulkhiretech.payments.pojo.PaymentResponse;
import com.hulkhiretech.payments.services.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(Constants.PAYMENTS_ENDPOINT)
@Slf4j
@RequiredArgsConstructor
public class PaymentsController {

	private final PaymentService paymentService;
	
	@PostMapping
	public PaymentResponse createPayment(
			@RequestBody PaymentRequest paymentDetails) {
		log.info("Received paymentDetails: {}", paymentDetails);

		// This method would typically handle payment creation logic
		PaymentResponse response = paymentService.createPayment(paymentDetails);

		log.info("Payment creation response: {}", response);

		return response;
	}

}