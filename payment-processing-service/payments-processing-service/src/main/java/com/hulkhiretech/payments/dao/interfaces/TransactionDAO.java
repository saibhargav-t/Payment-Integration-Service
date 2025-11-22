package com.hulkhiretech.payments.dao.interfaces;

import com.hulkhiretech.payments.entity.Transaction;

public interface TransactionDAO {

	public String saveTransaction(Transaction transaction);


	public Transaction getTransactionByReference(String txnReference);

	boolean updateTransaction(Transaction entity);
}
