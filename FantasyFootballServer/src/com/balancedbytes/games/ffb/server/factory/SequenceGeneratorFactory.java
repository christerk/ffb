package com.balancedbytes.games.ffb.server.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.factory.INamedObjectFactory;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.step.generator.SequenceGenerator;
import com.balancedbytes.games.ffb.util.Scanner;

import java.util.HashMap;
import java.util.Map;

@FactoryType(FactoryType.Factory.SEQUENCE_GENERATOR)
public class SequenceGeneratorFactory implements INamedObjectFactory<SequenceGenerator<? extends SequenceGenerator.SequenceParams>> {

	private final Map<String, SequenceGenerator<? extends SequenceGenerator.SequenceParams>> generators = new HashMap<>();

	@Override
	public SequenceGenerator<? extends SequenceGenerator.SequenceParams> forName(String name) {
		return generators.get(name);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void initialize(Game game) {

		new Scanner<>(SequenceGenerator.class).getSubclasses(game.getOptions()).forEach(
			generator -> {
				if (generators.containsKey(generator.getName())) {
					throw new FantasyFootballException("Duplicate sequence generator " + generator.getName() + " for " + game.getOptions().getRulesVersion().name());
				}
				generators.put(generator.getName(), generator);
			}
		);
	}
}
