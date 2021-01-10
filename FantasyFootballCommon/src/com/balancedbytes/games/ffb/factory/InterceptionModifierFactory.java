package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.InterceptionModifier;
import com.balancedbytes.games.ffb.InterceptionModifiers;
import com.balancedbytes.games.ffb.InterceptionModifiers.InterceptionContext;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilDisturbingPresence;
import com.balancedbytes.games.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.INTERCEPTION_MODIFIER)
@RulesCollection(Rules.COMMON)
public class InterceptionModifierFactory implements IRollModifierFactory<InterceptionModifier> {

	static InterceptionModifiers interceptionModifiers;

	public InterceptionModifierFactory() {
		interceptionModifiers = new InterceptionModifiers();
	}

	public InterceptionModifier forName(String pName) {
		return interceptionModifiers.values().get(pName.toLowerCase());
	}

	public Set<InterceptionModifier> findInterceptionModifiers(Game pGame, Player<?> pPlayer) {
		Set<InterceptionModifier> interceptionModifiers = activeModifiers(pGame, InterceptionModifier.class);

		InterceptionContext context = new InterceptionContext(pPlayer);
		interceptionModifiers.addAll(UtilCards.getInterceptionModifiers(pPlayer, context));

		if (!pPlayer.hasSkillWithProperty(NamedProperties.ignoreTacklezonesWhenCatching)) {
			InterceptionModifier tacklezoneModifier = getTacklezoneModifier(pGame, pPlayer);
			if (tacklezoneModifier != null) {
				interceptionModifiers.add(tacklezoneModifier);
			}
		}
		InterceptionModifier disturbingPresenceModifier = getDisturbingPresenceModifier(pGame, pPlayer);
		if (disturbingPresenceModifier != null) {
			interceptionModifiers.add(disturbingPresenceModifier);
		}
		if (UtilCards.hasCard(pGame, pGame.getThrower(), Card.FAWNDOUGHS_HEADBAND)) {
			interceptionModifiers.add(InterceptionModifiers.FAWNDOUGHS_HEADBAND);
		}
		if (UtilCards.hasCard(pGame, pPlayer, Card.MAGIC_GLOVES_OF_JARK_LONGARM)) {
			interceptionModifiers.add(InterceptionModifiers.MAGIC_GLOVES_OF_JARK_LONGARM);
		}
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

	private InterceptionModifier getTacklezoneModifier(Game pGame, Player<?> pPlayer) {
		int tacklezones = UtilPlayer.findTacklezones(pGame, pPlayer);
		for (Map.Entry<String, InterceptionModifier> entry : interceptionModifiers.values().entrySet()) {
			InterceptionModifier modifier = entry.getValue();
			if (modifier.isTacklezoneModifier() && (modifier.getModifier() == tacklezones)) {
				return modifier;
			}
		}
		return null;
	}

	private InterceptionModifier getDisturbingPresenceModifier(Game pGame, Player<?> pPlayer) {
		int disturbingPresences = UtilDisturbingPresence.findOpposingDisturbingPresences(pGame, pPlayer);
		for (Map.Entry<String, InterceptionModifier> entry : interceptionModifiers.values().entrySet()) {
			InterceptionModifier modifier = entry.getValue();
			if (modifier.isDisturbingPresenceModifier() && (modifier.getModifier() == disturbingPresences)) {
				return modifier;
			}
		}

		return null;
	}

	@Override
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
