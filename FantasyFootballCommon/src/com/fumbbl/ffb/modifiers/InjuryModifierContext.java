package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

public class InjuryModifierContext {
	private final Game game;
	private final InjuryContext injuryContext;
	private final Player<?> attacker;
	private final Player<?> defender;
	private final boolean isStab;
	private final boolean isFoul;
	private final boolean isVomit;
	private Mode mode;

	public InjuryModifierContext(Game game, InjuryContext injuryContext, Player<?> attacker, Player<?> defender,
	                             boolean isStab, boolean isFoul, boolean isVomit) {
		this.game = game;
		this.injuryContext = injuryContext;
		this.attacker = attacker;
		this.defender = defender;
		this.isStab = isStab;
		this.isFoul = isFoul;
		this.isVomit = isVomit;
		this.mode = Mode.ATTACKER;
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

	public boolean isVomit() {
		return isVomit;
	}

	public void setDefenderMode() {
		mode = Mode.DEFENDER;
	}

	public boolean isAttackerMode() {
		return mode == Mode.ATTACKER;
	}

	public boolean isDefenderMode() {
		return mode == Mode.DEFENDER;
	}

	private enum Mode {
		ATTACKER, DEFENDER
	}
}
