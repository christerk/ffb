package com.fumbbl.ffb.factory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.Scanner;

@FactoryType(FactoryType.Factory.MECHANIC)
@RulesCollection(RulesCollection.Rules.COMMON)
public class MechanicsFactory implements INamedObjectFactory<Mechanic> {

	private final Map<Mechanic.Type, Mechanic> mechanics = new HashMap<>();

	@Override
	public Mechanic forName(String pName) {
		return Arrays.stream(Mechanic.Type.values())
				.filter(type -> type.name().equals(pName))
				.findFirst()
				.map(mechanics::get)
				.orElse(null);
	}

	@Override
	public void initialize(Game game) {
		Scanner<Mechanic> scanner = new Scanner<>(Mechanic.class);

		scanner.getInstancesImplementing(game.getOptions())
				.forEach(mechanic -> mechanics.put(mechanic.getType(), mechanic));
	}
}
