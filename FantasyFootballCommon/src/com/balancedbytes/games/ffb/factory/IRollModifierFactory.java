package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.model.Game;

import java.util.Set;

/**
 * 
 * @author Kalimar
 */
public interface IRollModifierFactory<T extends IRollModifier> extends INamedObjectFactory<T> {

	IRollModifier forName(String pName);

	default Set<T> activeModifiers(Game game, Class<T> clazz) {
		return game.activeModifiers(clazz);
	}
}
