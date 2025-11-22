package com.hulkhiretech.payments.constants;

import lombok.Getter;

@Getter
public enum ProviderEnum {
	TRUSTLY(1, "TRUSTLY");

	private final int id;
	private final String name;

	ProviderEnum(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public static ProviderEnum getById(int id) {
		for (ProviderEnum value : values()) {
			if (value.id == id)
				return value;
		}
		throw new IllegalArgumentException("No ProviderEnum with id: " + id);
	}

	public static ProviderEnum getByName(String name) {
		for (ProviderEnum value : values()) {
			if (value.name.equalsIgnoreCase(name))
				return value;
		}
		throw new IllegalArgumentException("No ProviderEnum with name: " + name);
	}
}
