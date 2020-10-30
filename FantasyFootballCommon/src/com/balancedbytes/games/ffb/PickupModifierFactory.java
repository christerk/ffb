package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Kalimar
 */
public class PickupModifierFactory implements IRollModifierFactory {
  
  public PickupModifier forName(String pName) {
    for (PickupModifier modifier : PickupModifier.values()) {
      if (modifier.getName().equalsIgnoreCase(pName)) {
        return modifier;
      }
    }
    return null;
  }
  
  public Set<PickupModifier> findPickupModifiers(Game pGame) {
    Set<PickupModifier> pickupModifiers = new HashSet<PickupModifier>();
    Player player = pGame.getActingPlayer().getPlayer();
    if (player != null) {
      if (UtilCards.hasSkill(pGame, player, Skill.EXTRA_ARMS)) {
        pickupModifiers.add(PickupModifier.EXTRA_ARMS);
      }
      if (UtilCards.hasSkill(pGame, player, Skill.BIG_HAND)) {
        pickupModifiers.add(PickupModifier.BIG_HAND);
      } else {
        if (Weather.POURING_RAIN == pGame.getFieldModel().getWeather()) {
          pickupModifiers.add(PickupModifier.POURING_RAIN);
        }
        PickupModifier tacklezoneModifier = getTacklezoneModifier(pGame, player);
        if (tacklezoneModifier != null) {
          pickupModifiers.add(tacklezoneModifier);
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
  
  private PickupModifier getTacklezoneModifier(Game pGame, Player pPlayer) {
    int tacklezones = UtilPlayer.findTacklezones(pGame, pPlayer);
    if (tacklezones > 0) {
      for (PickupModifier modifier : PickupModifier.values()) {
        if (modifier.isTacklezoneModifier() && (modifier.getModifier() == tacklezones)) {
          return modifier;
        }
      }
    }
    return null;
  }
  
}
