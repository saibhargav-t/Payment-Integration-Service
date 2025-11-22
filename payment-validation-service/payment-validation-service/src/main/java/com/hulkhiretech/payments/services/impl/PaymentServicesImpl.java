package com.hulkhiretech.payments.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.constants.ValidatorEnum;
import com.hulkhiretech.payments.pojo.PaymentRequest;
import com.hulkhiretech.payments.pojo.PaymentResponse;
import com.hulkhiretech.payments.services.impl.validator.Validator;
import com.hulkhiretech.payments.services.interfaces.PaymentService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentServicesImpl implements PaymentService {
	
	@Value("${validator.rules}")
    private String validationRules;
	
	private ApplicationContext applicationContext;
	
	@Value("${mytestkey}")
	private String mytestkey;
	
	public PaymentServicesImpl(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public PaymentResponse createPayment(PaymentRequest paymentDetails) {
		log.info("Received payment details: {}", paymentDetails);
		
		// Split the validation rules and process each validator
		String[] rules = validationRules.split(",");
		for (String rule : rules) {
			log.info("Applying validation rule: {}", rule);
			
			Class<? extends Validator> validatorClass = 
					ValidatorEnum.getValidatorClassByName(rule);
			
			Validator validatorBean = null;
			if (validatorClass != null) {
				validatorBean = applicationContext.getBean(validatorClass);
			}
			
			if (validatorBean == null || validatorClass == null) {
				log.warn("Validator not found for rule: {}", rule);
				continue; // Skip if validator not found
			}
			
			log.info("Validator bean retrieved: {}", 
					validatorBean.getClass().getSimpleName());
			validatorBean.validate(paymentDetails);
			
		}
		
		//TODO this is temporary, replace with actual functional values.
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setId("12345");
		paymentResponse.setRedirectUrl(
				"https://example.com/redirect?paymentId=" 
		+ paymentResponse.getId());
		
		log.info("Payment response created: {}", paymentResponse);
		return paymentResponse;
	}
	
	@PostConstruct
	public void init() {
		log.info("****Calling init() mytestkey:{}", mytestkey);
	}
}