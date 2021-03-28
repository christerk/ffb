package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.modifiers.GazeModifier;
import com.balancedbytes.games.ffb.modifiers.GazeModifierCollection;
import com.balancedbytes.games.ffb.modifiers.GazeModifierContext;
import com.balancedbytes.games.ffb.modifiers.RollModifier;
import com.balancedbytes.games.ffb.util.Scanner;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.GAZE_MODIFIER)
@RulesCollection(Rules.COMMON)
public class GazeModifierFactory extends GenerifiedModifierFactory<GazeModifierContext, GazeModifier, GazeModifierCollection> {
	private GazeModifierCollection gazeModifierCollection = new GazeModifierCollection();

	@Override
	protected Scanner<GazeModifierCollection> getScanner() {
		return new Scanner<>(GazeModifierCollection.class);
	}

	@Override
	protected GazeModifierCollection getModifierCollection() {
		return gazeModifierCollection;
	}

	@Override
	protected void setModifierCollection(GazeModifierCollection modifierCollection) {
		this.gazeModifierCollection = modifierCollection;
	}

	@Override
	protected Collection<GazeModifier> getModifier(Skill skill) {
		return skill.getGazeModifiers();
	}

	@Override
	protected Optional<GazeModifier> checkClass(RollModifier<?> modifier) {
		return modifier instanceof GazeModifier ? Optional.of((GazeModifier) modifier) : Optional.empty();
	}

	@Override
	protected boolean isAffectedByDisturbingPresence(GazeModifierContext context) {
		return false;
	}

	@Override
	protected boolean isAffectedByTackleZones(GazeModifierContext context) {
		return true;
	}

	@Override
	public GazeModifier forName(String name) {
		return Stream.concat(
			gazeModifierCollection.getModifiers().stream(),
			modifierAggregator.getGazeModifiers().stream())
			.filter(modifier -> modifier.getName().equals(name))
			.findFirst()
			.orElse(null);
	}
}
