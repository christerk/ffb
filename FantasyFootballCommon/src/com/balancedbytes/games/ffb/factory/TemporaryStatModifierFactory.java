package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.modifiers.TemporaryStatModifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@FactoryType(FactoryType.Factory.TEMPORARY_STAT_MODIFIER)
@RulesCollection(RulesCollection.Rules.COMMON)
public class TemporaryStatModifierFactory implements INamedObjectFactory<TemporaryStatModifier> {
	@Override
	public TemporaryStatModifier forName(String name) {
		String[] parts = name.split("_");
		try {
			Constructor<?> constructor = Class.forName(parts[1]).getConstructor(TemporaryStatModifier.PlayerStat.class);
			return (TemporaryStatModifier) constructor.newInstance(TemporaryStatModifier.PlayerStat.valueOf(parts[0]));
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
			throw new FantasyFootballException("Could not create temporary stat modifier for '" + name + "' due to: ", e );
		}
	}

	@Override
	public void initialize(Game game) {
		// NOOP
	}
}