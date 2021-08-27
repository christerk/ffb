package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

public interface ModifierContext {
	Game getGame();

	Player<?> getPlayer();
}
