package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

public class GazeModifierContext implements ModifierContext {
	private final Game game;
	private final Player<?> player;

	public GazeModifierContext(Game game, Player<?> player) {
		this.game = game;
		this.player = player;
	}

	@Override
	public Game getGame() {
		return game;
	}

	@Override
	public Player<?> getPlayer() {
		return player;
	}
}
