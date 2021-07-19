package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.GazeModifier;
import com.fumbbl.ffb.modifiers.GazeModifierCollection;
import com.fumbbl.ffb.modifiers.GazeModifierContext;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.util.Scanner;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.GAZE_MODIFIER)
@RulesCollection(Rules.COMMON)
public class GazeModifierFactory extends GenerifiedModifierFactory<GazeModifierContext, GazeModifier, GazeModifierCollection> {
	private GazeModifierCollection gazeModifierCollection;

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
