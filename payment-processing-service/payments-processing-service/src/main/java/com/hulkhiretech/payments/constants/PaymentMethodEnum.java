package com.hulkhiretech.payments.constants;

import lombok.Getter;

@Getter
public enum PaymentMethodEnum {
	APM(1, "APM");

	private final int id;
	private final String name;

	PaymentMethodEnum(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public static PaymentMethodEnum getById(int id) {
		for (PaymentMethodEnum value : values()) {
			if (value.id == id)
				return value;
		}
		throw new IllegalArgumentException("No PaymentMethodEnum with id: " + id);
	}

	public static PaymentMethodEnum getByName(String name) {
		for (PaymentMethodEnum value : values()) {
			if (value.name.equalsIgnoreCase(name))
				return value;
		}
		throw new IllegalArgumentException("No PaymentMethodEnum with name: " + name);
	}
}
