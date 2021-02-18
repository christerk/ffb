package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

public class CatchContext implements ModifierContext {
	private final Player<?> player;
	private final CatchScatterThrowInMode catchMode;
	private final Game game;

	public CatchContext(Player<?> pPlayer, CatchScatterThrowInMode pCatchMode, Game game) {
		this.player = pPlayer;
		this.catchMode = pCatchMode;
		this.game = game;
	}

	public Game getGame() {
		return game;
	}

	public Player<?> getPlayer() {
		return player;
	}

	public CatchScatterThrowInMode getCatchMode() {
		return catchMode;
	}
}
