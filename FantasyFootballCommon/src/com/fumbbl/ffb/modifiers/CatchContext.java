package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

public class CatchContext implements ModifierContext {
	private final Player<?> player;
	private final CatchScatterThrowInMode catchMode;
	private final Game game;

	public CatchContext(Game game, Player<?> pPlayer, CatchScatterThrowInMode pCatchMode) {
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
