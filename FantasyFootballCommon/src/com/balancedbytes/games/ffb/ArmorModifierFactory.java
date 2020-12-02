package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.balancedbytes.games.ffb.ArmorModifiers.ArmorModifierContext;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 * 
 * @author Kalimar
 */
public class ArmorModifierFactory implements INamedObjectFactory {

	public ArmorModifier forName(String pName) {
		return ArmorModifiers.values().get(pName.toLowerCase());
	}
	
	public Set<ArmorModifier> findArmorModifiers(Game game, Player<?> attacker, Player<?> defender, boolean isStab, boolean isFoul) {
		 Set<ArmorModifier> armorModifiers = new HashSet<ArmorModifier>();
		 
		 ArmorModifierContext context = new ArmorModifierContext(game, attacker, defender, isStab, isFoul);
		 armorModifiers.addAll(UtilCards.getArmorModifiers(attacker, context));
		 
		 if(armorModifiers.contains(ArmorModifiers.CLAWS) 
				 && armorModifiers.contains(ArmorModifiers.MIGHTY_BLOW) 
				 && UtilGameOption.isOptionEnabled(game, GameOptionId.CLAW_DOES_NOT_STACK)) {
			armorModifiers.remove(ArmorModifiers.MIGHTY_BLOW);
		 }
	
		 return armorModifiers;
	}

	public ArmorModifier getFoulAssist(int pModifier) {
		for (Map.Entry<String, ArmorModifier> entry : ArmorModifiers.values().entrySet()) {
			ArmorModifier modifier = entry.getValue();
			if (modifier.isFoulAssistModifier() && (modifier.getModifier() == pModifier)) {
				return modifier;
			}
		}
		return null;
	}

	public ArmorModifier[] toArray(Set<ArmorModifier> pArmorModifiers) {
		if (pArmorModifiers != null) {
			ArmorModifier[] modifierArray = pArmorModifiers.toArray(new ArmorModifier[pArmorModifiers.size()]);
			Arrays.sort(
					modifierArray,
					new Comparator<ArmorModifier>() {
						public int compare(ArmorModifier pO1, ArmorModifier pO2) {
							return pO1.getName().compareTo(pO2.getName());
						}
					}
					);
			return modifierArray;
		} else {
			return new ArmorModifier[0];
		}
	}

}
