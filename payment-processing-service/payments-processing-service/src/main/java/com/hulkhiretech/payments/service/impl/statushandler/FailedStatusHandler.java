package com.hulkhiretech.payments.service.impl.statushandler;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.dao.interfaces.TransactionDAO;
import com.hulkhiretech.payments.dto.TransactionDto;
import com.hulkhiretech.payments.entity.Transaction;
import com.hulkhiretech.payments.service.interfaces.TransactionStatusHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FailedStatusHandler implements TransactionStatusHandler {

	private final TransactionDAO transactionDao;

	private final ModelMapper modelMapper;

	@Override
	public TransactionDto handleTransactionStatus(TransactionDto transactionDTO) {
		log.info("Handling transaction status for DTO: {}", transactionDTO);

		transactionDao.updateTransaction(modelMapper.map(transactionDTO, Transaction.class));
		log.info("Transaction status updated in database for transactionDTO: {}", transactionDTO);

		return transactionDTO;
	}

}
