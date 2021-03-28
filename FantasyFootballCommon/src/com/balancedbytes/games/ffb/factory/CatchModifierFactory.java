package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.modifiers.RollModifier;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.CatchContext;
import com.balancedbytes.games.ffb.modifiers.CatchModifier;
import com.balancedbytes.games.ffb.modifiers.CatchModifierCollection;
import com.balancedbytes.games.ffb.util.Scanner;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

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
