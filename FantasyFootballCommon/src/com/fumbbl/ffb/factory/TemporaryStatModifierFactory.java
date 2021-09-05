package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.StatsMechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.modifiers.PlayerStatKey;
import com.fumbbl.ffb.modifiers.TemporaryStatModifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@FactoryType(FactoryType.Factory.TEMPORARY_STAT_MODIFIER)
@RulesCollection(RulesCollection.Rules.COMMON)
public class TemporaryStatModifierFactory implements INamedObjectFactory<TemporaryStatModifier> {

	private Game game;

	@Override
	public TemporaryStatModifier forName(String name) {
		StatsMechanic mechanic = (StatsMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.STAT.name());
		String[] parts = name.split(TemporaryStatModifier.NAME_SEPARATOR);
		try {
			Constructor<?> constructor = Class.forName(parts[1]).getConstructor(PlayerStatKey.class, StatsMechanic.class);
			return (TemporaryStatModifier) constructor.newInstance(PlayerStatKey.valueOf(parts[0]), mechanic);
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
			throw new FantasyFootballException("Could not create temporary stat modifier for '" + name + "' due to: ", e);
		}
	}

	@Override
	public void initialize(Game game) {
		this.game = game;
	}
}
