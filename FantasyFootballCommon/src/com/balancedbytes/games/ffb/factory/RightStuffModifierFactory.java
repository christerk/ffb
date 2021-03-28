package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.modifiers.RollModifier;
import com.balancedbytes.games.ffb.modifiers.RightStuffContext;
import com.balancedbytes.games.ffb.modifiers.RightStuffModifier;
import com.balancedbytes.games.ffb.modifiers.RightStuffModifierCollection;
import com.balancedbytes.games.ffb.util.Scanner;

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

	private RightStuffModifierCollection rightStuffModifierCollection = new RightStuffModifierCollection();

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
