package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.model.Player;

public class CatchContext {
	public Player<?> player;
	public CatchScatterThrowInMode catchMode;

	public CatchContext(Player<?> pPlayer, CatchScatterThrowInMode pCatchMode) {
		this.player = pPlayer;
		this.catchMode = pCatchMode;
	}
}
