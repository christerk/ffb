package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.mechanics.PassResult;
import com.balancedbytes.games.ffb.model.Player;

public class InterceptionContext implements ModifierContext {
	private Player<?> player;
	private PassResult passResult;

	public InterceptionContext(Player<?> player) {
		this.player = player;
	}

	public Player<?> getPlayer() {
		return player;
	}

	public PassResult getPassResult() {
		return passResult;
	}
}
