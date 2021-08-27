package com.fumbbl.ffb.factory;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.CatchContext;
import com.fumbbl.ffb.modifiers.CatchModifier;
import com.fumbbl.ffb.modifiers.CatchModifierCollection;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.util.Scanner;

/**
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.CATCH_MODIFIER)
@RulesCollection(Rules.COMMON)
public class CatchModifierFactory extends GenerifiedModifierFactory<CatchContext, CatchModifier, CatchModifierCollection> {

	private CatchModifierCollection catchModifiers;

	@Override
	protected Collection<CatchModifier> getModifier(Skill skill) {
		return skill.getCatchModifiers();
	}

	@Override
	protected Optional<CatchModifier> checkClass(RollModifier<?> modifier) {
		return modifier instanceof  CatchModifier ? Optional.of((CatchModifier) modifier) : Optional.empty();
	}

	@Override
	protected boolean isAffectedByDisturbingPresence(CatchContext context) {
		return true;
	}

	@Override
	protected boolean isAffectedByTackleZones(CatchContext context) {
		return !context.getPlayer().hasSkillProperty(NamedProperties.ignoreTacklezonesWhenCatching);
	}

	@Override
	protected Scanner<CatchModifierCollection> getScanner() {
		return new Scanner<>(CatchModifierCollection.class);
	}

	@Override
	protected CatchModifierCollection getModifierCollection() {
		return catchModifiers;
	}

	@Override
	protected void setModifierCollection(CatchModifierCollection modifierCollection) {
		catchModifiers = modifierCollection;
	}

	@Override
	public CatchModifier forName(String name) {
		return Stream.concat(
			catchModifiers.getModifiers().stream(),
			modifierAggregator.getCatchModifiers().stream())
			.filter(modifier -> modifier.getName().equals(name))
			.findFirst()
			.orElse(null);
	}
}
