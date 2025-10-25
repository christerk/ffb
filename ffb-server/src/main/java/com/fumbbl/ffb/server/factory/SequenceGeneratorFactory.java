package com.fumbbl.ffb.server.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.util.Scanner;

import java.util.HashMap;
import java.util.Map;

@FactoryType(FactoryType.Factory.SEQUENCE_GENERATOR)
@RulesCollection(RulesCollection.Rules.COMMON)
public class SequenceGeneratorFactory implements INamedObjectFactory<SequenceGenerator<? extends SequenceGenerator.SequenceParams>> {

	private final Map<String, SequenceGenerator<? extends SequenceGenerator.SequenceParams>> generators = new HashMap<>();

	@Override
	public SequenceGenerator<? extends SequenceGenerator.SequenceParams> forName(String name) {
		return generators.get(name);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void initialize(Game game) {

		new Scanner<>(SequenceGenerator.class).getSubclassInstances(game.getOptions()).forEach(
			generator -> {
				if (generators.containsKey(generator.getName())) {
					throw new FantasyFootballException("Duplicate sequence generator " + generator.getName() + " for " + game.getOptions().getRulesVersion().name());
				}
				generators.put(generator.getName(), generator);
			}
		);
	}
}
