package com.fumbbl.ffb.model;

import java.util.Arrays;

public enum Keyword {
	VAMPIRE_LORD("Vampire Lord"),
	THRALL("Thrall"),
	DWARF("Dwarf"),
	MASTER_CHEF("Master Chef"),
	LINEMAN("Lineman"),
	BIG_GUY("Big Guy"),
	UNKNOWN("unknown");

	private final String name;

	Keyword(String name) {
		this.name = name;
	}

	public static Keyword forName(String name) {
		return Arrays.stream(values()).filter(keyword -> keyword.name.equalsIgnoreCase(name)).findFirst().orElse(UNKNOWN);
	}

	public String getName() {
		return name;
	}
}
