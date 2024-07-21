package com.fumbbl.ffb;

import com.fumbbl.ffb.modifiers.PlayerStatKey;

import java.util.Arrays;

/**
 * 
 * @author Kalimar
 */
public enum InjuryAttribute {

	MA(1, "MA"), ST(2, "ST"), AG(3, "AG"), AV(4, "AV"), NI(5, "NI"), PA(6, "PA");

	private final int fId;
	private final String fName;

	InjuryAttribute(int pValue, String pName) {
		fId = pValue;
		fName = pName;
	}

	public int getId() {
		return fId;
	}

	public String getName() {
		return fName;
	}

	public static InjuryAttribute forName(String rawName) {
		String name = rawName.replaceFirst("[+\\-]", "");
		return Arrays.stream(values()).filter(value -> name.equalsIgnoreCase(value.getName())).findFirst().orElse(null);
	}

	public static InjuryAttribute forStatKey(PlayerStatKey key) {
		return forName(key.name());
	}
}
