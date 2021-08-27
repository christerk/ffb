package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

public class PickupContext implements ModifierContext {
	private final Player<?> player;
	private final Game game;

	public PickupContext(Game game, Player<?> player) {
		this.player = player;
		this.game = game;
	}

	@Override
	public Player<?> getPlayer() {
		return player;
	}

	@Override
	public Game getGame() {
		return game;
	}
}
