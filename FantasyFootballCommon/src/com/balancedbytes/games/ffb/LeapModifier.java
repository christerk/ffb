package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 * 
 * @author Kalimar
 */
public enum LeapModifier implements IRollModifier {
  
  VERY_LONG_LEGS(1, "Very Long Legs", -1);
  
  private int fId;
  private String fName;
  private int fModifier;
  
  private LeapModifier(int pId, String pName, int pModifier) {
    fId = pId;
    fName = pName;
    fModifier = pModifier;
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

  public static LeapModifier fromId(int pId) {
    for (LeapModifier modifier : values()) {
      if (modifier.getId() == pId) {
        return modifier;
      }
    }
    return null;
  }
  
  public static LeapModifier fromName(String pName) {
    for (LeapModifier modifier : values()) {
      if (modifier.getName().equalsIgnoreCase(pName)) {
        return modifier;
      }
    }
    return null;
  }

  public static Set<LeapModifier> findLeapModifiers(Game pGame) {
    Set<LeapModifier> leapModifiers = new HashSet<LeapModifier>();
    ActingPlayer actingPlayer = pGame.getActingPlayer();
    if (UtilCards.hasSkill(pGame, actingPlayer, Skill.VERY_LONG_LEGS)) {
      leapModifiers.add(LeapModifier.VERY_LONG_LEGS);
    }
    return leapModifiers;
  }
  
  public static LeapModifier[] toArray(Set<LeapModifier> pLeapModifierSet) {
    if (pLeapModifierSet != null) {
      LeapModifier[] leapModifierArray = pLeapModifierSet.toArray(new LeapModifier[pLeapModifierSet.size()]);
      Arrays.sort(
        leapModifierArray,
        new Comparator<LeapModifier>() {
          public int compare(LeapModifier pO1, LeapModifier pO2) {
            return (pO1.getId() - pO2.getId());
          }
        }
      );
      return leapModifierArray;
    } else {
      return new LeapModifier[0];
    }
  }
  
  public boolean isModifierIncluded() {
    return false;
  }
  
}
