package com.fumbbl.ffb.inducement;

public class CardReport {
	private final String roll, description;

	public CardReport(String roll, String description) {
		this.roll = roll;
		this.description = description;
	}

	public String getRoll() {
		return roll;
	}

	public String getDescription() {
		return description;
	}
}
