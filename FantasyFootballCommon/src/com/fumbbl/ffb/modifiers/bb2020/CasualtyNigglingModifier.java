package com.fumbbl.ffb.modifiers.bb2020;

import java.util.Arrays;

import com.fumbbl.ffb.InjuryAttribute;
import com.fumbbl.ffb.model.Player;

public class CasualtyNigglingModifier extends CasualtyModifier {

	public CasualtyNigglingModifier(String name, int modifier) {
		super(name, modifier);
	}

	@Override
	public boolean appliesToContext(Player<?> player) {
		int nigglings =  (int) Arrays.stream(player.getLastingInjuries()).filter(injury -> injury.getInjuryAttribute() == InjuryAttribute.NI).count();
		return nigglings == getModifier();
	}

	@Override
	public String reportString() {
		return getName();
	}
}
