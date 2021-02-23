package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

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
