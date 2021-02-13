package com.balancedbytes.games.ffb.modifiers;

import java.util.Arrays;

public enum CatchModifierKey implements ModifierKey {
	DUMMY("Dummy"),
	ACCURATE("Accurate Pass"),
	NERVES_OF_STEEL("Nerves of Steel"),
	EXTRA_ARMS("Extra Arms"),
	POURING_RAIN("Pouring Rain"),
	TACKLEZONES_1("1 Tacklezone"),
	TACKLEZONES_2("2 Tacklezones"),
	TACKLEZONES_3("3 Tacklezones"),
	TACKLEZONES_4("4 Tacklezones"),
	TACKLEZONES_5("5 Tacklezones"),
	TACKLEZONES_6("6 Tacklezones"),
	TACKLEZONES_7("7 Tacklezones"),
	TACKLEZONES_8("8 Tacklezones"),
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
	DIVING_CATCH("Divign Catch"),
	HAND_OFF("Hand Off");
	
	private final String name;

	CatchModifierKey(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public static CatchModifierKey from(String name) {
		return Arrays.stream(values()).filter(key -> key.name.equals(name)).findFirst().orElse(DUMMY);
	}
}
