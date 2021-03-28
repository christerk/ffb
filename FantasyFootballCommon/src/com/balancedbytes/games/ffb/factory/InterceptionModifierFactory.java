package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.modifiers.RollModifier;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.InterceptionContext;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifier;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifierCollection;
import com.balancedbytes.games.ffb.util.Scanner;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

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
