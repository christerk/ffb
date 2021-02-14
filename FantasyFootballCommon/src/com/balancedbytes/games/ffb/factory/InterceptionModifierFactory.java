package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.Card;
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
import com.balancedbytes.games.ffb.modifiers.InterceptionModifierKey;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifierRegistry;
import com.balancedbytes.games.ffb.util.Scanner;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilDisturbingPresence;
import com.balancedbytes.games.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.INTERCEPTION_MODIFIER)
@RulesCollection(Rules.COMMON)
public class InterceptionModifierFactory extends GenerifiedModifierFactory<InterceptionModifierKey, InterceptionModifier, InterceptionModifierRegistry> {

	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private InterceptionModifierRegistry interceptionModifiers;

	private final InterceptionModifier dummy = new InterceptionModifier(InterceptionModifierKey.DUMMY, 0, false, false);

	@Override
	public InterceptionModifier forName(String pName) {
		return forKey(InterceptionModifierKey.from(pName));
	}

	public Set<InterceptionModifier> findInterceptionModifiers(Game pGame, Player<?> pPlayer, PassResult passResult) {
		Set<InterceptionModifier> interceptionModifiers = activeModifiers(pGame, InterceptionModifier.class);

		InterceptionContext context = new InterceptionContext(pPlayer);
		interceptionModifiers.addAll(getInterceptionModifiers(pPlayer, context));

		interceptionModifiers.add(forPassResult(passResult));

		if (!pPlayer.hasSkillWithProperty(NamedProperties.ignoreTacklezonesWhenCatching)) {
			getTacklezoneModifier(pGame, pPlayer).ifPresent(interceptionModifiers::add);
		}

		getDisturbingPresenceModifier(pGame, pPlayer).ifPresent(interceptionModifiers::add);

		if (UtilCards.hasCard(pGame, pGame.getThrower(), Card.FAWNDOUGHS_HEADBAND)) {
			interceptionModifiers.add(forKey(InterceptionModifierKey.FAWNDOUGHS_HEADBAND));
		}
		if (UtilCards.hasCard(pGame, pPlayer, Card.MAGIC_GLOVES_OF_JARK_LONGARM)) {
			interceptionModifiers.add(forKey(InterceptionModifierKey.MAGIC_GLOVES_OF_JARK_LONGARM));
		}

		interceptionModifiers.remove(dummy);

		return interceptionModifiers;
	}

	public InterceptionModifier[] toArray(Set<InterceptionModifier> pInterceptionModifierSet) {
		if (pInterceptionModifierSet != null) {
			InterceptionModifier[] interceptionModifierArray = pInterceptionModifierSet
				.toArray(new InterceptionModifier[0]);
			Arrays.sort(interceptionModifierArray, Comparator.comparing(InterceptionModifier::getName));
			return interceptionModifierArray;
		} else {
			return new InterceptionModifier[0];
		}
	}

	private Optional<InterceptionModifier> getTacklezoneModifier(Game pGame, Player<?> pPlayer) {
		int tacklezones = UtilPlayer.findTacklezones(pGame, pPlayer);
		return interceptionModifiers.values().stream()
			.filter(modifier -> modifier.isTacklezoneModifier() && (modifier.getMultiplier() == tacklezones))
			.findFirst();
	}

	private Optional<InterceptionModifier> getDisturbingPresenceModifier(Game pGame, Player<?> pPlayer) {
		int disturbingPresences = UtilDisturbingPresence.findOpposingDisturbingPresences(pGame, pPlayer);
		return interceptionModifiers.values().stream()
			.filter(modifier -> modifier.isDisturbingPresenceModifier() && (modifier.getMultiplier() == disturbingPresences))
			.findFirst();
	}

	@Override
	protected Scanner<InterceptionModifierRegistry> getScanner() {
		return new Scanner<>(InterceptionModifierRegistry.class);
	}

	@Override
	protected InterceptionModifierRegistry getRegistry() {
		return interceptionModifiers;
	}

	@Override
	protected void setRegistry(InterceptionModifierRegistry registry) {
		interceptionModifiers = registry;
	}

	@Override
	protected InterceptionModifier dummy() {
		return dummy;
	}

	private Collection<InterceptionModifier> getInterceptionModifiers(Player<?> player,
	                                                                  InterceptionContext context) {
		Set<InterceptionModifier> result = new HashSet<>();

		for (Skill skill : player.getSkills()) {
			for (InterceptionModifierKey modifierKey : skill.getInterceptionModifiers()) {
				InterceptionModifier modifier = forKey(modifierKey);
				if (modifier.appliesToContext(skill, context)) {
					result.add(modifier);
				}
			}
		}
		return result;
	}

	private InterceptionModifier forPassResult(PassResult passResult) {
		switch (passResult) {
			case ACCURATE:
				return forKey(InterceptionModifierKey.PASS_ACCURATE);
			case INACCURATE:
				return forKey(InterceptionModifierKey.PASS_INACCURATE);
			case WILDLY_INACCURATE:
				return forKey(InterceptionModifierKey.PASS_WILDLY_INACCURATE);
			default:
				return dummy;
		}
	}
}
