package com.fumbbl.ffb.model;

import java.util.Arrays;

public enum SpecialRule {
	SYLVANIAN_SPOTLIGHT("Sylvanian Spotlight"),
	BRIBERY_AND_CORRUPTION("Bribery and Corruption"),
	FAVOURED_OF_NURGLE("Favoured of Nurgle"),
	LOW_COST_LINEMEN("Low Cost Linemen"),
	SWARMING("Swarming"),
	MASTERS_OF_UNDEATH("Masters of Undeath");

	private final String ruleName;

	SpecialRule(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getRuleName() {
		return ruleName;
	}

	public static SpecialRule from(String name) {
		return Arrays.stream(values()).filter(rule -> rule.ruleName.equals(name)).findFirst().orElse(null);
	}

}
