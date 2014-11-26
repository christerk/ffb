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
public class GazeModifierFactory implements IRollModifierFactory {
  
  public GazeModifier forId(int pId) {
    for (GazeModifier modifier : GazeModifier.values()) {
      if (modifier.getId() == pId) {
        return modifier;
      }
    }
    return null;
  }
  
  public GazeModifier forName(String pName) {
    for (GazeModifier modifier : GazeModifier.values()) {
      if (modifier.getName().equalsIgnoreCase(pName)) {
        return modifier;
      }
    }
    return null;
  }
  
  public Set<GazeModifier> findGazeModifiers(Game pGame) {
    Set<GazeModifier> gazeModifiers = new HashSet<GazeModifier>();
    Player player = pGame.getActingPlayer().getPlayer();
    if (player != null) {
      GazeModifier tacklezoneModifier = getTacklezoneModifier(pGame, player);
      if (tacklezoneModifier != null) {
        gazeModifiers.add(tacklezoneModifier);
      }
    }
    return gazeModifiers;
  }
 
  public GazeModifier[] toArray(Set<GazeModifier> pGazeModifierSet) {
    if (pGazeModifierSet != null) {
      GazeModifier[] gazeModifierArray = pGazeModifierSet.toArray(new GazeModifier[pGazeModifierSet.size()]);
      Arrays.sort(
        gazeModifierArray,
        new Comparator<GazeModifier>() {
          public int compare(GazeModifier pO1, GazeModifier pO2) {
            return (pO1.getId() - pO2.getId());
          }
        }
      );
      return gazeModifierArray;
    } else {
      return new GazeModifier[0];
    }
  }
  
  private GazeModifier getTacklezoneModifier(Game pGame, Player pPlayer) {
    int tacklezones = UtilPlayer.findTacklezones(pGame, pPlayer);
    if (tacklezones > 1) {
      for (GazeModifier modifier : GazeModifier.values()) {
        if (modifier.isTacklezoneModifier() && (modifier.getModifier() == tacklezones - 1)) {
          return modifier;
        }
      }
    }
    return null;
  }
  
}
