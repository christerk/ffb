package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.Scanner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
