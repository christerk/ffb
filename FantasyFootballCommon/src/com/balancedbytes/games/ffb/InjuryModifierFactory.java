package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.balancedbytes.games.ffb.InjuryModifier.InjuryModifierContext;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 * 
 * @author Kalimar
 */
public class InjuryModifierFactory implements INamedObjectFactory {

	static InjuryModifiers injuryModifiers;

	public InjuryModifierFactory() {
		injuryModifiers = new InjuryModifiers();
	}

	public InjuryModifier forName(String pName) {
		return injuryModifiers.values().get(pName.toLowerCase());
	}

	public Set<InjuryModifier> findInjuryModifiers(Game game, InjuryContext injuryContext, Player<?> attacker,
			Player<?> defender, boolean isStab, boolean isFoul) {
		Set<InjuryModifier> injuryModifiers = new HashSet<InjuryModifier>();

		InjuryModifierContext context = new InjuryModifierContext(game, injuryContext, attacker, defender, isStab, isFoul);
		injuryModifiers.addAll(UtilCards.getInjuryModifiers(attacker, context));

		return injuryModifiers;
	}

	public InjuryModifier[] toArray(Set<InjuryModifier> pInjuryModifiers) {
		if (pInjuryModifiers != null) {
			InjuryModifier[] modifierArray = pInjuryModifiers.toArray(new InjuryModifier[pInjuryModifiers.size()]);
			Arrays.sort(modifierArray, new Comparator<InjuryModifier>() {
				public int compare(InjuryModifier pO1, InjuryModifier pO2) {
					return pO1.getName().compareTo(pO2.getName());
				}
			});
			return modifierArray;
		} else {
			return new InjuryModifier[0];
		}
	}

	public InjuryModifier getNigglingInjuryModifier(Player<?> pPlayer) {
		if (pPlayer != null) {
			int nigglingInjuries = 0;
			for (SeriousInjury injury : pPlayer.getLastingInjuries()) {
				if (InjuryAttribute.NI == injury.getInjuryAttribute()) {
					nigglingInjuries++;
				}
			}
			for (Map.Entry<String, InjuryModifier> entry : injuryModifiers.values().entrySet()) {
				InjuryModifier modifier = entry.getValue();
				if (modifier.isNigglingInjuryModifier() && (modifier.getModifier() == nigglingInjuries)) {
					return modifier;
				}
			}
		}
		return null;
	}

}
