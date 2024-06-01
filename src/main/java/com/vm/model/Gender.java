package com.vm.model;

public enum Gender {
	MALE("Nam"),
	FEMALE("Nữ"),
	OTHER("Khác");

	private String value;

	Gender(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}
}
