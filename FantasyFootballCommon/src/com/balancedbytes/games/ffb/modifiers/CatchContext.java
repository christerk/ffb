package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.model.Player;

public class CatchContext implements ModifierContext {
	private Player<?> player;
	private CatchScatterThrowInMode catchMode;

	public CatchContext(Player<?> pPlayer, CatchScatterThrowInMode pCatchMode) {
		this.player = pPlayer;
		this.catchMode = pCatchMode;
	}

	public Player<?> getPlayer() {
		return player;
	}

	public CatchScatterThrowInMode getCatchMode() {
		return catchMode;
	}
}
