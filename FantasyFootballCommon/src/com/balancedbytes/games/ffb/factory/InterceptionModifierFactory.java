package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.mechanics.PassResult;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.InterceptionContext;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifier;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifierCollection;
import com.balancedbytes.games.ffb.util.Scanner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
			interceptionModifiers.getAllModifiers().stream(),
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
	protected Set<InterceptionModifier> findModifiersInternal(InterceptionContext context) {

		Set<InterceptionModifier> interceptionModifiers = new HashSet<>();
		Game game = context.getGame();
		Player<?> player = context.getPlayer();

		getModifierCollection().getOtherModifiers().stream().filter(modifier -> modifier.appliesToContext(context))
			.forEach(interceptionModifiers::add);

		if (!player.hasSkillWithProperty(NamedProperties.ignoreTacklezonesWhenCatching)) {
			getTacklezoneModifier(game, player).ifPresent(interceptionModifiers::add);
		}

		getDisturbingPresenceModifier(game, player).ifPresent(interceptionModifiers::add);

/*		if (UtilCards.hasCard(game, game.getThrower(), Card.FAWNDOUGHS_HEADBAND)) {
			interceptionModifiers.add(forKey(InterceptionModifier.FAWNDOUGHS_HEADBAND));
		}
		if (UtilCards.hasCard(game, player, Card.MAGIC_GLOVES_OF_JARK_LONGARM)) {
			interceptionModifiers.add(forKey(InterceptionModifierKey.MAGIC_GLOVES_OF_JARK_LONGARM));
		}*/
		return interceptionModifiers;
	}

	@Override
	protected Optional<InterceptionModifier> checkClass(IRollModifier<?> modifier) {
		return modifier instanceof  InterceptionModifier ? Optional.of((InterceptionModifier) modifier) : Optional.empty();
	}

}
