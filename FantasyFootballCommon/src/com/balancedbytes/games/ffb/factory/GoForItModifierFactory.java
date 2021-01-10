package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.GoForItModifier;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.UtilCards;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.GO_FOR_IT_MODIFIER)
@RulesCollection(Rules.COMMON)
public class GoForItModifierFactory implements IRollModifierFactory<GoForItModifier> {

	public GoForItModifier forName(String pName) {
		for (GoForItModifier modifier : GoForItModifier.values()) {
			if (modifier.getName().equalsIgnoreCase(pName)) {
				return modifier;
			}
		}
		return null;
	}

	public Set<GoForItModifier> findGoForItModifiers(Game pGame) {
		Set<GoForItModifier> goForItModifiers = activeModifiers(pGame, GoForItModifier.class);

		if (UtilCards.isCardActive(pGame, Card.GREASED_SHOES)) {
			goForItModifiers.add(GoForItModifier.GREASED_SHOES);
		}
		return goForItModifiers;
	}

	public GoForItModifier[] toArray(Set<GoForItModifier> pGoForItModifierSet) {
		if (pGoForItModifierSet != null) {
			GoForItModifier[] goForItModifierArray = pGoForItModifierSet
					.toArray(new GoForItModifier[0]);
			Arrays.sort(goForItModifierArray, Comparator.comparing(GoForItModifier::getName));
			return goForItModifierArray;
		} else {
			return new GoForItModifier[0];
		}
	}

	@Override
	public void initialize(Game game) {
	}

}
