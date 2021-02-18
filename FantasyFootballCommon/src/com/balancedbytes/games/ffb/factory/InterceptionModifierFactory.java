package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
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
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.INTERCEPTION_MODIFIER)
@RulesCollection(Rules.COMMON)
public class InterceptionModifierFactory extends GenerifiedModifierFactory<
	InterceptionContext, InterceptionModifierFactory.InterceptionModifierCalculationInput,
	InterceptionModifier, InterceptionModifierCollection> {

	private InterceptionModifierCollection interceptionModifiers;

	@Override
	public InterceptionModifier forName(String name) {
		return dictionary.interceptionModifier(name);
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
	protected void setModifierCollection(InterceptionModifierCollection registry) {
		interceptionModifiers = registry;
	}

	@Override
	protected Collection<InterceptionModifier> getModifier(Skill skill) {
		return skill.getInterceptionModifiers();
	}

	@Override
	protected Set<InterceptionModifier> gameModifiers(Game game) {
		return activeModifiers(game, InterceptionModifier.class);
	}

	@Override
	protected Set<InterceptionModifier> findModifiersInternal(InterceptionModifierCalculationInput input) {

		Set<InterceptionModifier> interceptionModifiers = new HashSet<>();
		Game game = input.getGame();
		Player<?> player = input.getPlayer();

		getModifierCollection().getOtherModifiers().stream().filter(modifier -> modifier.appliesToContext(null, input.getContext()))
			.forEach(interceptionModifiers::add);

		if (!player.hasSkillWithProperty(NamedProperties.ignoreTacklezonesWhenCatching)) {
			getTacklezoneModifier(game, player).ifPresent(interceptionModifiers::add);
		}

		getDisturbingPresenceModifier(game, player).ifPresent(interceptionModifiers::add);

/*		if (UtilCards.hasCard(game, game.getThrower(), Card.FAWNDOUGHS_HEADBAND)) {
			interceptionModifiers.add(forKey(InterceptionModifierKey.FAWNDOUGHS_HEADBAND));
		}
		if (UtilCards.hasCard(game, player, Card.MAGIC_GLOVES_OF_JARK_LONGARM)) {
			interceptionModifiers.add(forKey(InterceptionModifierKey.MAGIC_GLOVES_OF_JARK_LONGARM));
		}*/
		return interceptionModifiers;
	}

	public static class InterceptionModifierCalculationInput extends GenerifiedModifierFactory.ModifierCalculationInput<InterceptionContext> {
		private final PassResult passResult;

		public InterceptionModifierCalculationInput(Game game, Player<?> player, PassResult passResult) {
			super(game, player);
			this.passResult = passResult;
		}

		public PassResult getPassResult() {
			return passResult;
		}

		@Override
		public InterceptionContext getContext() {
			return new InterceptionContext(getPlayer());
		}
	}
}
