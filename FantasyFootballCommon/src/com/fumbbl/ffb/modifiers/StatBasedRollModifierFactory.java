package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.Player;

public class StatBasedRollModifierFactory {
	private final String name;
	private final PlayerStatKey statKey;

	public StatBasedRollModifierFactory(String name, PlayerStatKey statKey) {
		this.name = name;
		this.statKey = statKey;
	}

	public StatBasedRollModifier create(Player<?> player) {
		return new StatBasedRollModifier(name, value(player));
	}

	private int value(Player<?> player) {
		switch (statKey) {
			case AG:
				return player.getAgilityWithModifiers();
			case AV:
				return player.getArmourWithModifiers();
			case MA:
				return player.getMovementWithModifiers();
			case PA:
				return player.getPassingWithModifiers();
			case ST:
				return player.getStrengthWithModifiers();
			default:
				return 0;
		}
	}
}
