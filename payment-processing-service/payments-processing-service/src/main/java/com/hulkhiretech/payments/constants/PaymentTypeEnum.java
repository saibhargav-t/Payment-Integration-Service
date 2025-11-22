package com.hulkhiretech.payments.constants;

import lombok.Getter;

@Getter
public enum PaymentTypeEnum {
	SALE(1, "SALE");

	private final int id;
	private final String name;

	PaymentTypeEnum(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public static PaymentTypeEnum getById(int id) {
		for (PaymentTypeEnum value : values()) {
			if (value.id == id)
				return value;
		}
		throw new IllegalArgumentException("No PaymentTypeEnum with id: " + id);
	}

	public static PaymentTypeEnum getByName(String name) {
		for (PaymentTypeEnum value : values()) {
			if (value.name.equalsIgnoreCase(name))
				return value;
		}
		throw new IllegalArgumentException("No PaymentTypeEnum with name: " + name);
	}
}
