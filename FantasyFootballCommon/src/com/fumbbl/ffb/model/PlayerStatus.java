package com.fumbbl.ffb.model;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.util.StringTool;

public enum PlayerStatus implements INamedObject {

	ACTIVE, JOURNEYMAN;

	public static PlayerStatus forName(String name) {
		if (!StringTool.isProvided(name)) {
			return null;
		}
		try {
			return valueOf(name.toUpperCase());
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	@Override
	public String getName() {
		return name().toLowerCase();
	}
}
