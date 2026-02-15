package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.kickoff.KickoffResult;
import com.fumbbl.ffb.kickoff.KickoffResultMapping;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.Scanner;

import java.util.Collection;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.KICKOFF_RESULT)
@RulesCollection(Rules.COMMON)
public class KickoffResultFactory implements INamedObjectFactory<KickoffResult> {

	private KickoffResultMapping mapping;

	public KickoffResult forName(String pName) {
		for (KickoffResult kickoff : mapping.getValues()) {
			if (kickoff.getName().equalsIgnoreCase(pName)) {
				return kickoff;
			}
		}
		return null;
	}

	public KickoffResult forRoll(int pRoll) {
		return mapping.getResult(pRoll);
	}

	@Override
	public void initialize(Game game) {
		new Scanner<>(KickoffResultMapping.class).getSubclassInstances(game.getOptions())
			.stream().findFirst().ifPresent(mapping -> this.mapping = mapping);
	}

	public Collection<? extends KickoffResult> allResults() {
		return mapping.getValues();
	}
}
