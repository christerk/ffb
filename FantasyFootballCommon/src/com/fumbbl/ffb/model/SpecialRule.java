package com.fumbbl.ffb.model;

import java.util.Arrays;

public enum SpecialRule {
	BADLANDS_BRAWL("Badlands Brawl"),
	ELVEN_KINGDOMS_LEAGUE("Elven Kingdoms League"),
	HALFLING_THIMBLE_CUP("Halfling Thimble Cup"),
	LUSTRIAN_SUPERLEAGUE("Lustrian Superleague"),
	OLD_WORLD_CLASSIC("Old World Classic"),
	SYLVANIAN_SPOTLIGHT("Sylvanian Spotlight"),
	UNDERWORLD_CHALLENGE("Underworld Challenge"),
	WORLDS_EDGE_SUPERLEAGUE("Worlds Edge Superleague"),
	BRIBERY_AND_CORRUPTION("Bribery and Corruption"),
	FAVOURED_OF_UNDIVIDED("Favoured of Chaos Undivided"),
	FAVOURED_OF_KHORNE("Favoured of Khorne"),
	FAVOURED_OF_NURGLE("Favoured of Nurgle"),
	FAVOURED_OF_TZEENTCH("Favoured of Tzeentch"),
	FAVOURED_OF_SLAANESH("Favoured of Slaanesh"),
	LOW_COST_LINEMEN("Low Cost Linemen"),
	MASTERS_OF_UNDEATH("Masters of Undeath");

	private final String ruleName;

	SpecialRule(String ruleName) {
		this.ruleName = ruleName;
	}

	public static SpecialRule from(String name) {
		return Arrays.stream(values()).filter(rule -> rule.ruleName.equals(name)).findFirst().orElse(null);
	}

}
