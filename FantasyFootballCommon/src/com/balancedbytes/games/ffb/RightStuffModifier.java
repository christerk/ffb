package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Kalimar
 */
public enum RightStuffModifier implements IRollModifier {
  
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
  
  private RightStuffModifier(int pId, String pName, int pModifier, boolean pTacklezoneModifier) {
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
  
  public static RightStuffModifier fromId(int pId) {
    for (RightStuffModifier modifier : values()) {
      if (modifier.getId() == pId) {
        return modifier;
      }
    }
    return null;
  }
  
  public static RightStuffModifier fromName(String pName) {
    for (RightStuffModifier modifier : values()) {
      if (modifier.getName().equalsIgnoreCase(pName)) {
        return modifier;
      }
    }
    return null;
  }

  public static Set<RightStuffModifier> findRightStuffModifiers(Game pGame, Player pPlayer) {
    Set<RightStuffModifier> rightStuffModifiers = new HashSet<RightStuffModifier>();
    RightStuffModifier tacklezoneModifier = getTacklezoneModifier(pGame, pPlayer);
    if (tacklezoneModifier != null) {
      rightStuffModifiers.add(tacklezoneModifier);
    }
    return rightStuffModifiers;
  }
  
  public static RightStuffModifier[] toArray(Set<RightStuffModifier> pRightStuffModifierSet) {
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
  
  private static RightStuffModifier getTacklezoneModifier(Game pGame, Player pPlayer) {
    int tacklezones = UtilPlayer.findTacklezones(pGame, pPlayer);
    for (RightStuffModifier modifier : values()) {
      if (modifier.isTacklezoneModifier() && (modifier.getModifier() == tacklezones)) {
        return modifier;
      }
    }
    return null;
  }
  
  public boolean isModifierIncluded() {
    return isTacklezoneModifier();
  }

}
