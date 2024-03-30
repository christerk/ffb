package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

public class InterceptionContext implements ModifierContext {
	private final Player<?> player;
	private final PassResult passResult;
	private final Game game;
	private final boolean bomb;

	public InterceptionContext(Game game, Player<?> player, PassResult passResult, boolean bomb) {
		this.player = player;
		this.passResult = passResult;
		this.game = game;
		this.bomb = bomb;
	}

	public boolean isBomb() {
		return bomb;
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
