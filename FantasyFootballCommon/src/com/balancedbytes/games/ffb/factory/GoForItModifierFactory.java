package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.modifiers.GoForItContext;
import com.balancedbytes.games.ffb.modifiers.GoForItModifier;
import com.balancedbytes.games.ffb.modifiers.GoForItModifierCollection;
import com.balancedbytes.games.ffb.modifiers.RollModifier;
import com.balancedbytes.games.ffb.util.Scanner;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.GO_FOR_IT_MODIFIER)
@RulesCollection(Rules.COMMON)
public class GoForItModifierFactory extends GenerifiedModifierFactory<GoForItContext, GoForItModifier, GoForItModifierCollection> {

	private GoForItModifierCollection goForItModifierCollection = new GoForItModifierCollection();

	public GoForItModifier forName(String name) {
		return Stream.concat(
			goForItModifierCollection.getModifiers().stream(),
			modifierAggregator.getGoForItModifiers().stream())
			.filter(modifier -> modifier.getName().equals(name))
			.findFirst()
			.orElse(null);
	}


	@Override
	protected Scanner<GoForItModifierCollection> getScanner() {
		return new Scanner<>(GoForItModifierCollection.class);
	}

	@Override
	protected GoForItModifierCollection getModifierCollection() {
		return goForItModifierCollection;
	}

	@Override
	protected void setModifierCollection(GoForItModifierCollection modifierCollection) {
		this.goForItModifierCollection = modifierCollection;
	}

	@Override
	protected Collection<GoForItModifier> getModifier(Skill skill) {
		return skill.getGoForItModifiers();
	}

	@Override
	protected Optional<GoForItModifier> checkClass(RollModifier<?> modifier) {
		return modifier instanceof GoForItModifier ? Optional.of((GoForItModifier) modifier) : Optional.empty();
	}

	@Override
	protected boolean isAffectedByDisturbingPresence(GoForItContext context) {
		return false;
	}

	@Override
	protected boolean isAffectedByTackleZones(GoForItContext context) {
		return false;
	}
}
