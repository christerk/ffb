package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

public class InjuryModifierContext {
	private final Game game;
	private final InjuryContext injuryContext;
	private final Player<?> attacker;
	private final Player<?> defender;
	private final boolean isStab;
	private final boolean isFoul;

	public InjuryModifierContext(Game game, InjuryContext injuryContext, Player<?> attacker, Player<?> defender,
	                             boolean isStab, boolean isFoul) {
		this.game = game;
		this.injuryContext = injuryContext;
		this.attacker = attacker;
		this.defender = defender;
		this.isStab = isStab;
		this.isFoul = isFoul;
	}

	public Game getGame() {
		return game;
	}

	public InjuryContext getInjuryContext() {
		return injuryContext;
	}

	public Player<?> getAttacker() {
		return attacker;
	}

	public Player<?> getDefender() {
		return defender;
	}

	public boolean isStab() {
		return isStab;
	}

	public boolean isFoul() {
		return isFoul;
	}
}
