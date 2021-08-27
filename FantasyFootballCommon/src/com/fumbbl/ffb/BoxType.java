package com.fumbbl.ffb;

/**
 * 
 * @author Kalimar
 */
public enum BoxType {

	RESERVES(1, "reserves", "Rsv", "player is in reserve.", "players are in reserve."),
	OUT(2, "out", "Out", "player is out of the game.", "players are out of the game.");

	private int fId;
	private String fName;
	private String fShortcut;
	private String fToolTipSingle;
	private String fToolTipMultiple;

	private BoxType(int pValue, String pName, String pShortcut, String pToolTipSingle, String pToolTipMultiple) {
		fId = pValue;
		fName = pName;
		fShortcut = pShortcut;
		fToolTipSingle = pToolTipSingle;
		fToolTipMultiple = pToolTipMultiple;
	}

	public int getId() {
		return fId;
	}

	public String getName() {
		return fName;
	}

	public String getShortcut() {
		return fShortcut;
	}

	public String getToolTipSingle() {
		return fToolTipSingle;
	}

	public String getToolTipMultiple() {
		return fToolTipMultiple;
	}

	public static BoxType fromId(int pId) {
		for (BoxType type : values()) {
			if (type.getId() == pId) {
				return type;
			}
		}
		return null;
	}

	public static BoxType fromName(String pName) {
		for (BoxType type : values()) {
			if (type.getName().equalsIgnoreCase(pName)) {
				return type;
			}
		}
		return null;
	}

}
