package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

public interface ModifierContext {
	Game getGame();

	Player<?> getPlayer();
}
