package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.PassContext;
import com.balancedbytes.games.ffb.modifiers.PassModifier;
import com.balancedbytes.games.ffb.modifiers.PassModifierCollection;
import com.balancedbytes.games.ffb.util.Scanner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.PASS_MODIFIER)
@RulesCollection(Rules.COMMON)
public class PassModifierFactory extends GenerifiedModifierFactory<PassContext, PassModifier, PassModifierCollection> {

	private PassModifierCollection passModifierCollection;

	public PassModifier forName(String name) {
		return Stream.concat(
			passModifierCollection.getAllModifiers().stream(),
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
	protected Set<PassModifier> findModifiersInternal(PassContext context) {
		Player<?> thrower = context.getPlayer();
		Game game = context.getGame();
		Set<PassModifier> passModifiers = new HashSet<>();

		getModifierCollection().getOtherModifiers().stream()
			.filter(passModifier -> passModifier.appliesToContext(context))
			.forEach(passModifiers::add);

		if (!context.getPlayer().hasSkillWithProperty(NamedProperties.ignoreTacklezonesWhenPassing)) {
			getTacklezoneModifier(game, thrower).ifPresent(passModifiers::add);
		}

		getDisturbingPresenceModifier(game, thrower).ifPresent(passModifiers::add);

		return passModifiers;
	}

	@Override
	protected Optional<PassModifier> checkClass(IRollModifier<?> modifier) {
		return modifier instanceof PassModifier ? Optional.of((PassModifier) modifier) : Optional.empty();
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
		passModifierCollection = modifierCollection;
	}

}
