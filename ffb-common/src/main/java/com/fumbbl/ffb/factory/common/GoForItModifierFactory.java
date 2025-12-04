package com.fumbbl.ffb.factory.common;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.GenerifiedModifierFactory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.GoForItContext;
import com.fumbbl.ffb.modifiers.GoForItModifier;
import com.fumbbl.ffb.modifiers.GoForItModifierCollection;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.util.Scanner;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.GO_FOR_IT_MODIFIER)
@RulesCollection(RulesCollection.Rules.COMMON)
public class GoForItModifierFactory extends GenerifiedModifierFactory<GoForItContext, GoForItModifier, GoForItModifierCollection> {

	protected GoForItModifierCollection goForItModifierCollection;

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
