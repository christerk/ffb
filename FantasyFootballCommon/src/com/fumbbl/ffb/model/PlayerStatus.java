package com.fumbbl.ffb.model;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.util.StringTool;

public enum PlayerStatus implements INamedObject {

	ACTIVE, JOURNEYMAN;

	public static PlayerStatus forName(String name) {
		if (!StringTool.isProvided(name)) {
			return null;
		}
		return valueOf(name.toUpperCase());
	}

	@Override
	public String getName() {
		return name().toLowerCase();
	}
}
