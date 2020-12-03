package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Kalimar
 */
public class PickupModifierFactory implements IRollModifierFactory {
	
	static PickupModifiers pickupModifiers;
	
	public PickupModifierFactory()
	{
		pickupModifiers = new PickupModifiers();
	}
  
  public PickupModifier forName(String pName) {
	  return pickupModifiers.values().get(pName.toLowerCase());
  }
  
  public Set<PickupModifier> findPickupModifiers(Game pGame) {
    Set<PickupModifier> pickupModifiers = new HashSet<PickupModifier>();
    Player<?> player = pGame.getActingPlayer().getPlayer();
    if (player != null) {
      
      pickupModifiers.addAll(UtilCards.getPickupModifiers(player, null));
      
      if (!UtilCards.hasSkillWithProperty(player, NamedProperties.ignoreTacklezonesWhenPickingUp)) {
        PickupModifier tacklezoneModifier = getTacklezoneModifier(pGame, player);
        if (tacklezoneModifier != null) {
          pickupModifiers.add(tacklezoneModifier);
        }
      }
      
      if (!UtilCards.hasSkillWithProperty(player, NamedProperties.ignoreWeatherWhenPickingUp)) {
        if (Weather.POURING_RAIN == pGame.getFieldModel().getWeather()) {
          pickupModifiers.add(PickupModifiers.POURING_RAIN);
        }
      }
    }
    return pickupModifiers;
  }
 
  public PickupModifier[] toArray(Set<PickupModifier> pPickupModifierSet) {
    if (pPickupModifierSet != null) {
      PickupModifier[] pickupModifierArray = pPickupModifierSet.toArray(new PickupModifier[pPickupModifierSet.size()]);
      Arrays.sort(
        pickupModifierArray,
        new Comparator<PickupModifier>() {
          public int compare(PickupModifier pO1, PickupModifier pO2) {
            return pO1.getName().compareTo(pO2.getName());
          }
        }
      );
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

}
