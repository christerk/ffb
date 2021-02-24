package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.modifiers.IRollModifier;
import com.balancedbytes.games.ffb.modifiers.LeapContext;
import com.balancedbytes.games.ffb.modifiers.LeapModifier;
import com.balancedbytes.games.ffb.modifiers.LeapModifierCollection;
import com.balancedbytes.games.ffb.util.Scanner;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.LEAP_MODIFIER)
@RulesCollection(Rules.COMMON)
public class LeapModifierFactory extends GenerifiedModifierFactory<LeapContext, LeapModifier, LeapModifierCollection> {

	private LeapModifierCollection leapModifierCollection = new LeapModifierCollection();

	public LeapModifier forName(String name) {
		return Stream.concat(
				leapModifierCollection.getModifiers().stream(),
				modifierAggregator.getLeapModifiers().stream())
				.filter(modifier -> modifier.getName().equals(name))
				.findFirst()
				.orElse(null);	}


	@Override
	protected Scanner<LeapModifierCollection> getScanner() {
		return new Scanner<>(LeapModifierCollection.class);
	}

	@Override
	protected LeapModifierCollection getModifierCollection() {
		return leapModifierCollection;
	}

	@Override
	protected void setModifierCollection(LeapModifierCollection modifierCollection) {
		this.leapModifierCollection = modifierCollection;
	}

	@Override
	protected Collection<LeapModifier> getModifier(Skill skill) {
		return skill.getLeapModifiers();
	}

	@Override
	protected Optional<LeapModifier> checkClass(IRollModifier<?> modifier) {
		return modifier instanceof LeapModifier ? Optional.of((LeapModifier) modifier) : Optional.empty();
	}

	@Override
	protected boolean isAffectedByDisturbingPresence(LeapContext context) {
		return false;
	}

	@Override
	protected boolean isAffectedByTackleZones(LeapContext context) {
		return false;
	}
}
