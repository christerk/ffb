package com.balancedbytes.games.ffb.modifiers.bb2020;

import com.balancedbytes.games.ffb.InjuryAttribute;
import com.balancedbytes.games.ffb.model.Player;

import java.util.Arrays;

public class CasualtyNigglingModifier extends CasualtyModifier {

	public CasualtyNigglingModifier(String name, int modifier) {
		super(name, modifier);
	}

	@Override
	public boolean appliesToContext(Player<?> player) {
		int nigglings =  (int) Arrays.stream(player.getLastingInjuries()).filter(injury -> injury.getInjuryAttribute() == InjuryAttribute.NI).count();
		return nigglings == getModifier();
	}
}
