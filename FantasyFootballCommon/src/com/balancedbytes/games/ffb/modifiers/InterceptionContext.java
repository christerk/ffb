package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.mechanics.PassResult;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

public class InterceptionContext implements ModifierContext {
	private final Player<?> player;
	private final PassResult passResult;
	private final Game game;

	public InterceptionContext(Player<?> player, PassResult passResult, Game game) {
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
