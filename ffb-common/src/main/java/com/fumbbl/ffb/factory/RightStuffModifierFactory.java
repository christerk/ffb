package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.RightStuffContext;
import com.fumbbl.ffb.modifiers.RightStuffModifier;
import com.fumbbl.ffb.modifiers.RightStuffModifierCollection;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.util.Scanner;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.RIGHT_STUFF_MODIFIER)
@RulesCollection(Rules.COMMON)
public class RightStuffModifierFactory extends GenerifiedModifierFactory<RightStuffContext, RightStuffModifier, RightStuffModifierCollection> {

	private RightStuffModifierCollection rightStuffModifierCollection;

	public RightStuffModifier forName(String name) {
		return Stream.concat(
			rightStuffModifierCollection.getModifiers().stream(),
			modifierAggregator.getRightStuffModifiers().stream())
			.filter(modifier -> modifier.getName().equals(name))
			.findFirst()
			.orElse(null);
	}

	@Override
	protected Scanner<RightStuffModifierCollection> getScanner() {
		return new Scanner<>(RightStuffModifierCollection.class);
	}

	@Override
	protected RightStuffModifierCollection getModifierCollection() {
		return rightStuffModifierCollection;
	}

	@Override
	protected void setModifierCollection(RightStuffModifierCollection modifierCollection) {
		this.rightStuffModifierCollection = modifierCollection;
	}

	@Override
	protected Collection<RightStuffModifier> getModifier(Skill skill) {
		return skill.getRightStuffModifiers();
	}

	@Override
	protected Optional<RightStuffModifier> checkClass(RollModifier<?> modifier) {
		return modifier instanceof RightStuffModifier ? Optional.of((RightStuffModifier) modifier) : Optional.empty();
	}

	@Override
	protected boolean isAffectedByDisturbingPresence(RightStuffContext context) {
		return false;
	}

	@Override
	protected boolean isAffectedByTackleZones(RightStuffContext context) {
		return true;
	}
}
