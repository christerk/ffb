package com.balancedbytes.games.ffb.server.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.factory.INamedObjectFactory;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.util.Scanner;

import java.util.Optional;

@FactoryType(FactoryType.Factory.SEQUENCE_GENERATOR)
public class SequenceGeneratorFactory implements INamedObjectFactory<SequenceGenerator> {

	private SequenceGenerator generator;

	@Override
	public SequenceGenerator forName(String pName) {
		return generator;
	}

	public SequenceGenerator getInstance(){
		return generator;
	}

	@Override
	public void initialize(Game game) {
		Scanner<SequenceGenerator> scanner = new Scanner<>(SequenceGenerator.class);
		Optional<SequenceGenerator> generator = scanner.getSubclasses(game.getOptions()).stream().findFirst();
		if (generator.isPresent()) {
			this.generator = generator.get();
		} else {
			throw new FantasyFootballException("Could not find sequence generator for " + game.getOptions().getRulesVersion().name());
		}
	}
}
