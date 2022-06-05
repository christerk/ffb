package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

public class CatchContext implements ModifierContext {
	private final Player<?> player;
	private final CatchScatterThrowInMode catchMode;
	private final Game game;
	private final Boolean usingBlastIt;

	public CatchContext(Game game, Player<?> pPlayer, CatchScatterThrowInMode pCatchMode, Boolean usingBlastIt) {
		this.player = pPlayer;
		this.catchMode = pCatchMode;
		this.game = game;
		this.usingBlastIt = usingBlastIt;
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

	public Boolean getUsingBlastIt() {
		return usingBlastIt;
	}
}
