package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Kalimar
 */
public enum PickupModifier implements IRollModifier {
  
  BIG_HAND(1, "Big Hand", 0, false),
  POURING_RAIN(2, "Pouring Rain", 1, false),
  EXTRA_ARMS(3, "Extra Arms", -1, false),
  TACKLEZONES_1(4, "1 Tacklezone", 1, true),
  TACKLEZONES_2(5, "2 Tacklezones", 2, true),
  TACKLEZONES_3(6, "3 Tacklezones", 3, true),
  TACKLEZONES_4(7, "4 Tacklezones", 4, true),
  TACKLEZONES_5(8, "5 Tacklezones", 5, true),
  TACKLEZONES_6(9, "6 Tacklezones", 6, true),
  TACKLEZONES_7(10, "7 Tacklezones", 7, true),
  TACKLEZONES_8(11, "8 Tacklezones", 8, true);
  
  private int fId;
  private String fName;
  private int fModifier;
  private boolean fTacklezoneModifier;
  
  private PickupModifier(int pId, String pName, int pModifier, boolean pTacklezoneModifier) {
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
  
  public static PickupModifier fromId(int pId) {
    for (PickupModifier modifier : values()) {
      if (modifier.getId() == pId) {
        return modifier;
      }
    }
    return null;
  }
  
  public static PickupModifier fromName(String pName) {
    for (PickupModifier modifier : values()) {
      if (modifier.getName().equalsIgnoreCase(pName)) {
        return modifier;
      }
    }
    return null;
  }
  
  public static Set<PickupModifier> findPickupModifiers(Game pGame) {
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
        PickupModifier tacklezoneModifier = PickupModifier.getTacklezoneModifier(pGame, player);
        if (tacklezoneModifier != null) {
          pickupModifiers.add(tacklezoneModifier);
        }
      }
    }
    return pickupModifiers;
  }
 
  public static PickupModifier[] toArray(Set<PickupModifier> pPickupModifierSet) {
    if (pPickupModifierSet != null) {
      PickupModifier[] pickupModifierArray = pPickupModifierSet.toArray(new PickupModifier[pPickupModifierSet.size()]);
      Arrays.sort(
        pickupModifierArray,
        new Comparator<PickupModifier>() {
          public int compare(PickupModifier pO1, PickupModifier pO2) {
            return (pO1.getId() - pO2.getId());
          }
        }
      );
      return pickupModifierArray;
    } else {
      return new PickupModifier[0];
    }
  }
  
  private static PickupModifier getTacklezoneModifier(Game pGame, Player pPlayer) {
    int tacklezones = UtilPlayer.findTacklezones(pGame, pPlayer);
    if (tacklezones > 0) {
      for (PickupModifier modifier : values()) {
        if (modifier.isTacklezoneModifier() && (modifier.getModifier() == tacklezones)) {
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
