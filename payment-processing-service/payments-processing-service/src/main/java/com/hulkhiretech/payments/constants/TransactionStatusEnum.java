package com.hulkhiretech.payments.constants;

import lombok.Getter;

@Getter
public enum TransactionStatusEnum {
	CREATED(1, "CREATED"), INITIATED(2, "INITIATED"), PENDING(3, "PENDING"), SUCCESS(4, "SUCCESS"), FAILED(5, "FAILED");

	private final int id;
	private final String name;

	TransactionStatusEnum(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public static TransactionStatusEnum getById(int id) {
		for (TransactionStatusEnum value : values()) {
			if (value.id == id)
				return value;
		}
		throw new IllegalArgumentException("No TransactionStatusEnum with id: " + id);
	}

	public static TransactionStatusEnum getByName(String name) {
		for (TransactionStatusEnum value : values()) {
			if (value.name.equalsIgnoreCase(name))
				return value;
		}
		throw new IllegalArgumentException("No TransactionStatusEnum with name: " + name);
	}
}
