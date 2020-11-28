package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.LeapModifiers.LeapContext;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 * 
 * @author Kalimar
 */
public class LeapModifierFactory implements IRollModifierFactory {

	public LeapModifier forName(String pName) {
		return LeapModifiers.values().get(pName.toLowerCase());
	}

	public Set<LeapModifier> findLeapModifiers(Game pGame, FieldCoordinate pCoordinateFrom) {
		Set<LeapModifier> leapModifiers = new HashSet<LeapModifier>();
		ActingPlayer actingPlayer = pGame.getActingPlayer();

		LeapContext context = new LeapContext(actingPlayer, pCoordinateFrom);
		leapModifiers.addAll(UtilCards.getLeapModifiers(actingPlayer, context));


		return leapModifiers;
	}

	public LeapModifier[] toArray(Set<LeapModifier> pLeapModifierSet) {
		if (pLeapModifierSet != null) {
			LeapModifier[] leapModifierArray = pLeapModifierSet.toArray(new LeapModifier[pLeapModifierSet.size()]);
			Arrays.sort(
					leapModifierArray,
					new Comparator<LeapModifier>() {
						public int compare(LeapModifier pO1, LeapModifier pO2) {
							return pO1.getName().compareTo(pO2.getName());
						}
					}
					);
			return leapModifierArray;
		} else {
			return new LeapModifier[0];
		}
	}

}
