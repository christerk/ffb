package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

public class InterceptionContext implements ModifierContext {
	private final Player<?> player;
	private final PassResult passResult;
	private final Game game;

	public InterceptionContext(Game game, Player<?> player, PassResult passResult) {
		this.player = player;
		this.passResult = passResult;
		this.game = game;
	}

	public Player<?> getPlayer() {
		return player;
	}

	public PassResult getPassResult() {
		return passResult;
	}

	public Game getGame() {
		return game;
	}
}
