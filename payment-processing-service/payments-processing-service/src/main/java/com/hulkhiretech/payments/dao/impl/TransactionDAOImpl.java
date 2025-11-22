package com.hulkhiretech.payments.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.hulkhiretech.payments.dao.interfaces.TransactionDAO;
import com.hulkhiretech.payments.entity.Transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TransactionDAOImpl implements TransactionDAO {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public String saveTransaction(Transaction transaction) {
		log.info("Saving transaction: {}", transaction);
		String sql = """
				INSERT INTO payments.Transaction (userId,paymentMethodId,providerId,paymentTypeId,txnStatusId,amount,currency,
				merchantTransactionReference,txnReference,providerReference, retryCount) VALUES
				(:userId,:paymentMethodId,:providerId,:paymentTypeId,:txnStatusId,:amount,:currency,:merchantTransactionReference,
				:txnReference,:providerReference,:retryCount)""";

		BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(transaction);

		int rowsInserted = jdbcTemplate.update(sql, params);

		if (rowsInserted > 0) {
			log.info("Transaction saved successfully with reference: {}", transaction.getTxnReference());
			return "Your Transaction is saved with Ref No: " + transaction.getTxnReference()
					+ ". Please keep this for future reference.";
		} else {
			log.error("Failed to save transaction");
			return "Transaction save failed";
		}
	}

	@Override
	public boolean updateTransaction(Transaction entity) {
		log.info(
				"Updating transaction in DAO layer for txnReference:{} | txnStatusId:{} | providerReference:{} | errorCode:{} | errorMessage:{}",
				entity.getTxnReference(), entity.getTxnStatusId(), entity.getProviderReference(), entity.getErrorCode(),
				entity.getErrorMessage());

		String sql = "UPDATE payments.Transaction " + "SET txnStatusId = :txnStatusId, "
				+ "providerReference = :providerReference, " + "errorCode = :errorCode, "
				+ "errorMessage = :errorMessage " + "WHERE txnReference = :txnReference";

		Map<String, Object> params = new HashMap<>();
		params.put("txnStatusId", entity.getTxnStatusId());
		params.put("providerReference", entity.getProviderReference());
		params.put("errorCode", entity.getErrorCode());
		params.put("errorMessage", entity.getErrorMessage());
		params.put("txnReference", entity.getTxnReference());

		int updated = jdbcTemplate.update(sql, params);
		log.info("Transaction updated successfully for reference: {}, rows affected: {}", entity.getTxnReference(),
				updated);
		return updated > 0;
	}

	@Override
	public Transaction getTransactionByReference(String txnReference) {
		String sql = "SELECT * FROM payments.Transaction WHERE txnReference = :txnReference";

		Map<String, Object> params = new HashMap<>();
		params.put("txnReference", txnReference);

		Transaction entity = jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Transaction.class));
		log.info("Transaction retrieved successfully for reference: {}", txnReference);
		return entity;
	}
}
