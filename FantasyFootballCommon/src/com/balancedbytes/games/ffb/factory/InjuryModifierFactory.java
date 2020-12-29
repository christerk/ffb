package com.balancedbytes.games.ffb.factory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.InjuryAttribute;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.InjuryModifier;
import com.balancedbytes.games.ffb.InjuryModifiers;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.InjuryModifier.InjuryModifierContext;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 *
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.injuryModifier)
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

		InjuryModifierContext context = new InjuryModifierContext(game, injuryContext, attacker, defender, isStab, isFoul);

		return new HashSet<>(UtilCards.getInjuryModifiers(attacker, context));
	}

	public InjuryModifier[] toArray(Set<InjuryModifier> pInjuryModifiers) {
		if (pInjuryModifiers != null) {
			InjuryModifier[] modifierArray = pInjuryModifiers.toArray(new InjuryModifier[0]);
			Arrays.sort(modifierArray, Comparator.comparing(InjuryModifier::getName));
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

	@Override
	public void initialize(Rules rules, GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
