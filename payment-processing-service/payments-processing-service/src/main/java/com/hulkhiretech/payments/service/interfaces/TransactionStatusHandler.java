package com.hulkhiretech.payments.service.interfaces;

import com.hulkhiretech.payments.dto.TransactionDto;

public interface TransactionStatusHandler {

    public TransactionDto handleTransactionStatus(TransactionDto transactionDto);
}
