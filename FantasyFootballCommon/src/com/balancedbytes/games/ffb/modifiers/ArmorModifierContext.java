package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

public class ArmorModifierContext {
	public Game game;
	public Player<?> attacker;
	public Player<?> defender;
	public boolean isStab;
	public boolean isFoul;

	public ArmorModifierContext(Game game, Player<?> attacker, Player<?> defender, boolean isStab, boolean isFoul) {
		this.game = game;
		this.attacker = attacker;
		this.defender = defender;
		this.isStab = isStab;
		this.isFoul = isFoul;
	}
}
