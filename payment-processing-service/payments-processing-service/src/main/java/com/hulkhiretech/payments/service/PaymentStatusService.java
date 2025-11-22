package com.hulkhiretech.payments.service;

import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.constants.TransactionStatusEnum;
import com.hulkhiretech.payments.dto.TransactionDto;
import com.hulkhiretech.payments.service.factory.TransactionStatusFactory;
import com.hulkhiretech.payments.service.interfaces.TransactionStatusHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentStatusService {

    private final TransactionStatusFactory transactionStatusFactory;

    public TransactionDto updatePayment(TransactionDto transactionDto) {
    	TransactionStatusEnum statusEnum = TransactionStatusEnum.getByName(transactionDto.getTxnStatus());
    	log.info("The enum corresponding to the status code {} is {}", transactionDto.getTxnStatus(), statusEnum);
        TransactionStatusHandler transactionStatusHandler = transactionStatusFactory.getTransactionHandler(statusEnum);
        if(transactionStatusHandler==null){
            log.error("NO status handler found for the status code: {}",transactionDto.getTxnStatus());
            throw new RuntimeException("No Handler found for the status code: "+transactionDto.getTxnStatus());
        }
        transactionDto = transactionStatusHandler.handleTransactionStatus(transactionDto);
        return transactionDto;
    }

}
