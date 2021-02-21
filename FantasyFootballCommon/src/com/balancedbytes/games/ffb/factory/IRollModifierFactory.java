package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.modifiers.IRollModifier;
import com.balancedbytes.games.ffb.model.Game;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Kalimar
 */
public interface IRollModifierFactory<T extends IRollModifier> extends INamedObjectFactory<T> {

	IRollModifier forName(String pName);

	//TODO delete
	default Set<T> activeModifiers(Game game, Class<T> clazz) {
		return new HashSet<>();
	}
}
