package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public enum InjuryAttribute {

	MA(1, "MA"), ST(2, "ST"), AG(3, "AG"), AV(4, "AV"), NI(5, "NI"), PA(6, "PA");

	private int fId;
	private String fName;

	private InjuryAttribute(int pValue, String pName) {
		fId = pValue;
		fName = pName;
	}

	public int getId() {
		return fId;
	}

	public String getName() {
		return fName;
	}

	public static InjuryAttribute fromId(int pId) {
		for (InjuryAttribute attribute : values()) {
			if (attribute.getId() == pId) {
				return attribute;
			}
		}
		return null;
	}

	public static InjuryAttribute fromName(String pName) {
		for (InjuryAttribute attribute : values()) {
			if (attribute.getName().equals(pName)) {
				return attribute;
			}
		}
		return null;
	}

}
