package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.PickupModifier;
import com.balancedbytes.games.ffb.PickupModifiers;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.PICKUP_MODIFIER)
@RulesCollection(Rules.COMMON)
public class PickupModifierFactory implements IRollModifierFactory<PickupModifier> {

	static PickupModifiers pickupModifiers;

	public PickupModifierFactory() {
		pickupModifiers = new PickupModifiers();
	}

	public PickupModifier forName(String pName) {
		return pickupModifiers.values().get(pName.toLowerCase());
	}

	public Set<PickupModifier> findPickupModifiers(Game pGame) {
		Set<PickupModifier> pickupModifiers = new HashSet<>();
		Player<?> player = pGame.getActingPlayer().getPlayer();
		if (player != null) {

			pickupModifiers.addAll(UtilCards.getPickupModifiers(player, null));

			if (!player.hasSkillWithProperty(NamedProperties.ignoreTacklezonesWhenPickingUp)) {
				PickupModifier tacklezoneModifier = getTacklezoneModifier(pGame, player);
				if (tacklezoneModifier != null) {
					pickupModifiers.add(tacklezoneModifier);
				}
			}

			if (!player.hasSkillWithProperty(NamedProperties.ignoreWeatherWhenPickingUp)) {
				pickupModifiers.addAll(activeModifiers(pGame, PickupModifier.class));
			}
		}
		return pickupModifiers;
	}

	public PickupModifier[] toArray(Set<PickupModifier> pPickupModifierSet) {
		if (pPickupModifierSet != null) {
			PickupModifier[] pickupModifierArray = pPickupModifierSet.toArray(new PickupModifier[0]);
			Arrays.sort(pickupModifierArray, Comparator.comparing(PickupModifier::getName));
			return pickupModifierArray;
		} else {
			return new PickupModifier[0];
		}
	}

	private PickupModifier getTacklezoneModifier(Game pGame, Player<?> pPlayer) {
		int tacklezones = UtilPlayer.findTacklezones(pGame, pPlayer);
		if (tacklezones > 0) {
			for (Map.Entry<String, PickupModifier> entry : pickupModifiers.values().entrySet()) {
				PickupModifier modifier = entry.getValue();
				if (modifier.isTacklezoneModifier() && (modifier.getModifier() == tacklezones)) {
					return modifier;
				}
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
