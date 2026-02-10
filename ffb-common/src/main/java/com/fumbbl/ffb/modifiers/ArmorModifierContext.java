package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

public class ArmorModifierContext {
	private final Game game;
	private final Player<?> attacker;
	private final Player<?> defender;
	private final boolean isStab;
	private final boolean isFoul;
	private final int foulAssists;
	private final boolean isTtm;

	public ArmorModifierContext(Game game, Player<?> attacker, Player<?> defender, boolean isStab, boolean isFoul) {
		this(game, attacker, defender, isStab, isFoul, 0);
	}

	public ArmorModifierContext(Game game, Player<?> attacker, Player<?> defender, boolean isStab, boolean isFoul, int foulAssists) {
		this(game, attacker, defender, isStab, isFoul, foulAssists, false);
	}

	public ArmorModifierContext(Game game, Player<?> attacker, Player<?> defender, boolean isStab, boolean isFoul, int foulAssists, 
		boolean isTtm) {
		this.game = game;
		this.attacker = attacker;
		this.defender = defender;
		this.isStab = isStab;
		this.isFoul = isFoul;
		this.foulAssists = foulAssists;
		this.isTtm = isTtm;
	}

	public Game getGame() {
		return game;
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

	public int getFoulAssists() {
		return foulAssists;
	}

	public boolean isTtm() {
		return isTtm;
	}
}
