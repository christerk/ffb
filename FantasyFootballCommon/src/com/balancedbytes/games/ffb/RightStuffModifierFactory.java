package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Kalimar
 */
public class RightStuffModifierFactory implements IRollModifierFactory {
  
  public RightStuffModifier forId(int pId) {
    for (RightStuffModifier modifier : RightStuffModifier.values()) {
      if (modifier.getId() == pId) {
        return modifier;
      }
    }
    return null;
  }
  
  public RightStuffModifier forName(String pName) {
    for (RightStuffModifier modifier : RightStuffModifier.values()) {
      if (modifier.getName().equalsIgnoreCase(pName)) {
        return modifier;
      }
    }
    return null;
  }

  public Set<RightStuffModifier> findRightStuffModifiers(Game pGame, Player pPlayer) {
    Set<RightStuffModifier> rightStuffModifiers = new HashSet<RightStuffModifier>();
    RightStuffModifier tacklezoneModifier = getTacklezoneModifier(pGame, pPlayer);
    if (tacklezoneModifier != null) {
      rightStuffModifiers.add(tacklezoneModifier);
    }
    return rightStuffModifiers;
  }
  
  public RightStuffModifier[] toArray(Set<RightStuffModifier> pRightStuffModifierSet) {
    if (pRightStuffModifierSet != null) {
      RightStuffModifier[] rightStuffModifierArray = pRightStuffModifierSet.toArray(new RightStuffModifier[pRightStuffModifierSet.size()]);
      Arrays.sort(
          rightStuffModifierArray,
        new Comparator<RightStuffModifier>() {
          public int compare(RightStuffModifier pO1, RightStuffModifier pO2) {
            return (pO1.getId() - pO2.getId());
          }
        }
      );
      return rightStuffModifierArray;
    } else {
      return new RightStuffModifier[0];
    }
  }
  
  private RightStuffModifier getTacklezoneModifier(Game pGame, Player pPlayer) {
    int tacklezones = UtilPlayer.findTacklezones(pGame, pPlayer);
    for (RightStuffModifier modifier : RightStuffModifier.values()) {
      if (modifier.isTacklezoneModifier() && (modifier.getModifier() == tacklezones)) {
        return modifier;
      }
    }
    return null;
  }
  
}
