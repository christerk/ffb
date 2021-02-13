package com.balancedbytes.games.ffb.modifiers;

import java.util.Arrays;

public enum InterceptionModifierKey implements ModifierKey {
	DUMMY("Dummy"),
	DISTURBING_PRESENCES_1("1 Disturbing Presence"),
	DISTURBING_PRESENCES_2("2 Disturbing Presences"),
	DISTURBING_PRESENCES_3("3 Disturbing Presences"),
	DISTURBING_PRESENCES_4("4 Disturbing Presences"),
	DISTURBING_PRESENCES_5("5 Disturbing Presences"),
	DISTURBING_PRESENCES_6("6 Disturbing Presences"),
	DISTURBING_PRESENCES_7("7 Disturbing Presences"),
	DISTURBING_PRESENCES_8("8 Disturbing Presences"),
	DISTURBING_PRESENCES_9("9 Disturbing Presences"),
	DISTURBING_PRESENCES_10("10 Disturbing Presences"),
	DISTURBING_PRESENCES_11("11 Disturbing Presences"),
	EXTRA_ARMS("Extra Arms"),
	FAWNDOUGHS_HEADBAND("Fawndough's Headband"),
	MAGIC_GLOVES_OF_JARK_LONGARM("Magic Gloves of Jark Longarm"),
	NERVES_OF_STEEL("Nerves of Steel"),
	POURING_RAIN("Pouring Rain"),
	TACKLEZONES_1("1 Tacklezone"),
	TACKLEZONES_2("2 Tacklezones"),
	TACKLEZONES_3("3 Tacklezones"),
	TACKLEZONES_4("4 Tacklezones"),
	TACKLEZONES_5("5 Tacklezones"),
	TACKLEZONES_6("6 Tacklezones"),
	TACKLEZONES_7("7 Tacklezones"),
	TACKLEZONES_8("8 Tacklezones"),
	VERY_LONG_LEGS("Very Long Legs");

	private final String name;

	InterceptionModifierKey(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public static InterceptionModifierKey from(String name) {
		return Arrays.stream(values()).filter(key -> key.name.equals(name)).findFirst().orElse(DUMMY);
	}
}
