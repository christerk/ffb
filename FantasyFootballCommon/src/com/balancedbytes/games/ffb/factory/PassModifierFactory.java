package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.IRollModifier;
import com.balancedbytes.games.ffb.modifiers.PassContext;
import com.balancedbytes.games.ffb.modifiers.PassModifier;
import com.balancedbytes.games.ffb.modifiers.PassModifierCollection;
import com.balancedbytes.games.ffb.util.Scanner;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.PASS_MODIFIER)
@RulesCollection(Rules.COMMON)
public class PassModifierFactory extends GenerifiedModifierFactory<PassContext, PassModifier, PassModifierCollection> {

	private PassModifierCollection passModifierCollection = new PassModifierCollection();

	public PassModifier forName(String name) {
		return Stream.concat(
			passModifierCollection.getModifiers().stream(),
			modifierAggregator.getPassModifiers().stream())
			.filter(modifier -> modifier.getName().equals(name))
			.findFirst()
			.orElse(null);
	}

	@Override
	protected Collection<PassModifier> getModifier(Skill skill) {
		return skill.getPassModifiers();
	}

	@Override
	protected Optional<PassModifier> checkClass(IRollModifier<?> modifier) {
		return modifier instanceof PassModifier ? Optional.of((PassModifier) modifier) : Optional.empty();
	}

	@Override
	protected boolean isAffectedByDisturbingPresence(PassContext context) {
		return true;
	}

	@Override
	protected boolean isAffectedByTackleZones(PassContext context) {
		return !context.getPlayer().hasSkillWithProperty(NamedProperties.ignoreTacklezonesWhenPassing);
	}

	@Override
	protected Scanner<PassModifierCollection> getScanner() {
		return new Scanner<>(PassModifierCollection.class);
	}

	@Override
	protected PassModifierCollection getModifierCollection() {
		return passModifierCollection;
	}

	@Override
	protected void setModifierCollection(PassModifierCollection modifierCollection) {
		this.passModifierCollection = modifierCollection;
	}

}
