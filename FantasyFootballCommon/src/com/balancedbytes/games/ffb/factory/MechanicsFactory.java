package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.util.Scanner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@FactoryType(FactoryType.Factory.MECHANIC)
@RulesCollection(RulesCollection.Rules.COMMON)
public class MechanicsFactory implements INamedObjectFactory<Mechanic> {

	private Map<Mechanic.Type, Mechanic> mechanics = new HashMap<>();

	@Override
	public Mechanic forName(String pName) {
		return Arrays.stream(Mechanic.Type.values())
				.filter(type -> type.name().equals(pName))
				.findFirst()
				.map(type -> mechanics.get(type))
				.orElse(null);
	}

	@Override
	public void initialize(Game game) {
		Scanner<Mechanic> scanner = new Scanner<>(Mechanic.class);

		scanner.getClassesImplementing(game.getOptions())
				.forEach(mechanic -> mechanics.put(mechanic.getType(), mechanic));

		 String missingTypes = Arrays.stream(Mechanic.Type.values())
				.filter(type -> !mechanics.containsKey(type))
				.map(Mechanic.Type::name)
				.collect(Collectors.joining(", "));

		 if (!missingTypes.isEmpty()) {
		 	String rulesVersion = game.getOptions().getRulesVersion().name();
		 	throw new FantasyFootballException("No mechanic registered for type(s): " + missingTypes + " in rules version " + rulesVersion);
		 }
	}
}
