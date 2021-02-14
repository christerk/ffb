package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.model.Player;

public class InterceptionContext implements ModifierContext {
	public Player<?> player;

	public InterceptionContext(Player<?> player) {
		this.player = player;
	}
}
