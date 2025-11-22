package com.hulkhiretech.payments.service.impl;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.constants.ErrorEnum;
import com.hulkhiretech.payments.constants.TransactionStatusEnum;
import com.hulkhiretech.payments.dao.interfaces.TransactionDAO;
import com.hulkhiretech.payments.dto.TransactionDto;
import com.hulkhiretech.payments.entity.Transaction;
import com.hulkhiretech.payments.exception.TrustlyProviderException;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.pojo.CreateTransaction;
import com.hulkhiretech.payments.pojo.CreateTransactionResponse;
import com.hulkhiretech.payments.pojo.InitiateTxnRequest;
import com.hulkhiretech.payments.pojo.PaymentResponse;
import com.hulkhiretech.payments.service.PaymentServiceHelper;
import com.hulkhiretech.payments.service.PaymentStatusService;
import com.hulkhiretech.payments.service.interfaces.PaymentService;
import com.hulkhiretech.payments.trustlyprovider.TrustlyProviderDepositResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final ModelMapper modelMapper;
	private final PaymentStatusService paymentStatusService;
	private final TransactionDAO transactionDAO;
	private final PaymentServiceHelper paymentsServiceHelper;
	private final HttpServiceEngine httpServiceEngine;

	@Override
	public CreateTransactionResponse createPayment(CreateTransaction createTransaction) {
		log.info("Transaction received in PaymentServiceImpl:  {}", createTransaction);
		TransactionDto transactionDto = modelMapper.map(createTransaction, TransactionDto.class);
		transactionDto.setTxnStatus(TransactionStatusEnum.CREATED.getName());
		transactionDto.setTxnReference(UUID.randomUUID().toString());
		transactionDto.setRetryCount(0);
		log.info("Transaction Final: {}", transactionDto);
		transactionDto = paymentStatusService.updatePayment(transactionDto);
		CreateTransactionResponse response = new CreateTransactionResponse();
		response.setTxnStatus(transactionDto.getTxnStatus());
		response.setTxnReference(transactionDto.getTxnReference());
		log.info("The created transaction reference is: {}", response);
		return response;
	}

	@Override
	public PaymentResponse initiatePayment(String txnReference, InitiateTxnRequest initiateTxnRequest) {
		log.info("Initiating payment for txnReference: {}, initiateTxnRequest: {}", txnReference, initiateTxnRequest);

		Transaction txnEntity = transactionDAO.getTransactionByReference(txnReference);
		log.info("Fetched transaction entity: {}", txnEntity);

		TransactionDto txnDTO = modelMapper.map(txnEntity, TransactionDto.class);
		log.info("Mapped TransactionEntity to TransactionDTO: {}", txnDTO);

		txnDTO.setTxnStatus(TransactionStatusEnum.INITIATED.getName());
		txnDTO = paymentStatusService.updatePayment(txnDTO);

		log.info("Processed transactionDTO after initiation: {}", txnDTO);

		HttpRequest request = paymentsServiceHelper.prepareInitiateRequest(txnDTO, initiateTxnRequest);

		TrustlyProviderDepositResponse responseObj = null;
		try {
			ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(request);

			responseObj = paymentsServiceHelper.processResponse(httpResponse);
			log.info("Processed DepositResponse: {}", responseObj);

		} catch (TrustlyProviderException e) {// Failure in processing the response
			log.error("Error processing Trustly response: {}", e.getMessage(), e);
			txnDTO.setTxnStatus(TransactionStatusEnum.FAILED.getName());
			txnDTO.setErrorCode(e.getErrorCode());
			txnDTO.setErrorMessage(e.getErrorMessage());

			paymentStatusService.updatePayment(txnDTO);
			log.info("Transaction status updated to FAILED with error: {}", e.getErrorMessage());
			throw e;
		} catch (Exception e) {
			log.error("Error processing Trustly response: {}", e.getMessage(), e);
			txnDTO.setTxnStatus(TransactionStatusEnum.FAILED.getName());
			txnDTO.setErrorCode(ErrorEnum.ERROR_PROCESSING_TRUSTLY_RESPONSE.getErrorCode());
			txnDTO.setErrorMessage(ErrorEnum.ERROR_PROCESSING_TRUSTLY_RESPONSE.getErrorMessage());

			paymentStatusService.updatePayment(txnDTO);
			log.info("Transaction status updated to FAILED with error: {}",
					ErrorEnum.ERROR_PROCESSING_TRUSTLY_RESPONSE.getErrorMessage());
			throw e;
		}

		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setTxnReference(txnDTO.getTxnReference());
		paymentResponse.setUrl(responseObj.getUrl());

		txnDTO.setTxnStatus(TransactionStatusEnum.PENDING.getName());
		txnDTO.setProviderReference(responseObj.getOrderid());
		txnDTO = paymentStatusService.updatePayment(txnDTO);

		paymentResponse.setTxnStatus(txnDTO.getTxnStatus());
		log.info("Returning responseObj: {}", responseObj);

		return paymentResponse;

	}
}
