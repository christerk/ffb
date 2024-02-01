package com.fumbbl.ffb.factory;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.InterceptionContext;
import com.fumbbl.ffb.modifiers.InterceptionModifier;
import com.fumbbl.ffb.modifiers.InterceptionModifierCollection;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.util.Scanner;

/**
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.INTERCEPTION_MODIFIER)
@RulesCollection(Rules.COMMON)
public class InterceptionModifierFactory extends GenerifiedModifierFactory<InterceptionContext, InterceptionModifier, InterceptionModifierCollection> {

	private InterceptionModifierCollection interceptionModifiers;

	@Override
	public InterceptionModifier forName(String name) {
		return Stream.concat(
			interceptionModifiers.getModifiers().stream(),
			modifierAggregator.getInterceptionModifiers().stream())
			.filter(modifier -> modifier.getName().equals(name))
			.findFirst()
			.orElse(null);
	}

	@Override
	protected Scanner<InterceptionModifierCollection> getScanner() {
		return new Scanner<>(InterceptionModifierCollection.class);
	}

	@Override
	protected InterceptionModifierCollection getModifierCollection() {
		return interceptionModifiers;
	}

	@Override
	protected void setModifierCollection(InterceptionModifierCollection modifierCollection) {
		interceptionModifiers = modifierCollection;
	}

	@Override
	protected Collection<InterceptionModifier> getModifier(Skill skill) {
		return skill.getInterceptionModifiers();
	}

	@Override
	protected Optional<InterceptionModifier> checkClass(RollModifier<?> modifier) {
		return modifier instanceof  InterceptionModifier ? Optional.of((InterceptionModifier) modifier) : Optional.empty();
	}

	@Override
	protected boolean isAffectedByDisturbingPresence(InterceptionContext context) {
		return true;
	}

	@Override
	protected boolean isAffectedByTackleZones(InterceptionContext context) {
		return !context.getPlayer().hasSkillProperty(NamedProperties.ignoreTacklezonesWhenCatching);
	}

}
