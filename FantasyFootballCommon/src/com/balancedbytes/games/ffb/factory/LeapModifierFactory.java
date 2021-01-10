package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.LeapModifier;
import com.balancedbytes.games.ffb.LeapModifiers;
import com.balancedbytes.games.ffb.LeapModifiers.LeapContext;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.UtilCards;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.LEAP_MODIFIER)
@RulesCollection(Rules.COMMON)
public class LeapModifierFactory implements IRollModifierFactory<LeapModifier> {

	static LeapModifiers leapModifiers;

	public LeapModifierFactory() {
		leapModifiers = new LeapModifiers();
	}

	public LeapModifier forName(String pName) {
		return leapModifiers.values().get(pName.toLowerCase());
	}

	public Set<LeapModifier> findLeapModifiers(Game pGame, FieldCoordinate pCoordinateFrom) {
		ActingPlayer actingPlayer = pGame.getActingPlayer();

		LeapContext context = new LeapContext(actingPlayer, pCoordinateFrom);

		Set<LeapModifier> modifiers = activeModifiers(pGame, LeapModifier.class);
		modifiers.addAll(UtilCards.getLeapModifiers(actingPlayer, context));
		return modifiers;
	}

	public LeapModifier[] toArray(Set<LeapModifier> pLeapModifierSet) {
		if (pLeapModifierSet != null) {
			LeapModifier[] leapModifierArray = pLeapModifierSet.toArray(new LeapModifier[0]);
			Arrays.sort(leapModifierArray, Comparator.comparing(LeapModifier::getName));
			return leapModifierArray;
		} else {
			return new LeapModifier[0];
		}
	}

	@Override
	public void initialize(Game game) {
	}

}
