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
public enum GazeModifier implements IRollModifier {
  
  TACKLEZONES_1(1, "1 Tacklezone", 1, true),
  TACKLEZONES_2(2, "2 Tacklezones", 2, true),
  TACKLEZONES_3(3, "3 Tacklezones", 3, true),
  TACKLEZONES_4(4, "4 Tacklezones", 4, true),
  TACKLEZONES_5(5, "5 Tacklezones", 5, true),
  TACKLEZONES_6(6, "6 Tacklezones", 6, true),
  TACKLEZONES_7(7, "7 Tacklezones", 7, true),
  TACKLEZONES_8(8, "8 Tacklezones", 8, true);
  
  private int fId;
  private String fName;
  private int fModifier;
  private boolean fTacklezoneModifier;
  
  private GazeModifier(int pId, String pName, int pModifier, boolean pTacklezoneModifier) {
    fId = pId;
    fName = pName;
    fModifier = pModifier;
    fTacklezoneModifier = pTacklezoneModifier;
  }
    
  public int getId() {
    return fId;
  }
  
  public int getModifier() {
    return fModifier;
  }
  
  public String getName() {
    return fName;
  }

  public boolean isTacklezoneModifier() {
    return fTacklezoneModifier;
  }
  
  public static GazeModifier fromId(int pId) {
    for (GazeModifier modifier : values()) {
      if (modifier.getId() == pId) {
        return modifier;
      }
    }
    return null;
  }
  
  public static GazeModifier fromName(String pName) {
    for (GazeModifier modifier : values()) {
      if (modifier.getName().equalsIgnoreCase(pName)) {
        return modifier;
      }
    }
    return null;
  }
  
  public static Set<GazeModifier> findGazeModifiers(Game pGame) {
    Set<GazeModifier> gazeModifiers = new HashSet<GazeModifier>();
    Player player = pGame.getActingPlayer().getPlayer();
    if (player != null) {
      GazeModifier tacklezoneModifier = GazeModifier.getTacklezoneModifier(pGame, player);
      if (tacklezoneModifier != null) {
        gazeModifiers.add(tacklezoneModifier);
      }
    }
    return gazeModifiers;
  }
 
  public static GazeModifier[] toArray(Set<GazeModifier> pGazeModifierSet) {
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
  
  private static GazeModifier getTacklezoneModifier(Game pGame, Player pPlayer) {
    int tacklezones = UtilPlayer.findTacklezones(pGame, pPlayer);
    if (tacklezones > 1) {
      for (GazeModifier modifier : values()) {
        if (modifier.isTacklezoneModifier() && (modifier.getModifier() == tacklezones - 1)) {
          return modifier;
        }
      }
    }
    return null;
  }
  
  public boolean isModifierIncluded() {
    return isTacklezoneModifier();
  }
  
}
