package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

public class JumpUpContext implements ModifierContext {
	private final ActingPlayer actingPlayer;
	private final Game game;

	public JumpUpContext(ActingPlayer actingPlayer, Game game) {
		this.actingPlayer = actingPlayer;
		this.game = game;
	}

	@Override
	public Game getGame() {
		return game;
	}

	@Override
	public Player<?> getPlayer() {
		return actingPlayer.getPlayer();
	}
}
