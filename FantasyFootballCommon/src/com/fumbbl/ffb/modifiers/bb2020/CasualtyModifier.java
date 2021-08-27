package com.fumbbl.ffb.modifiers.bb2020;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.model.Player;

public class CasualtyModifier implements INamedObject {

	private final int modifier;
	private final String name;

	public CasualtyModifier(String name, int modifier) {
		this.modifier = modifier;
		this.name = name;
	}

	public int getModifier() {
		return modifier;
	};

	public String getName() {
		return name;
	}

	public boolean appliesToContext(Player<?> player) {
		return true;
	}

	public String reportString() {
		return modifier + " " + name;
	}
}
