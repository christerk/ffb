package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public enum GoForItModifier implements IRollModifier {
  
  BLIZZARD(1, "Blizzard", 1);
  
  private int fId;
  private String fName;
  private int fModifier;
  
  private GoForItModifier(int pId, String pName, int pModifier) {
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

  public static GoForItModifier fromId(int pId) {
    for (GoForItModifier modifier : values()) {
      if (modifier.getId() == pId) {
        return modifier;
      }
    }
    return null;
  }
  
  public static GoForItModifier fromName(String pName) {
    for (GoForItModifier modifier : values()) {
      if (modifier.getName().equalsIgnoreCase(pName)) {
        return modifier;
      }
    }
    return null;
  }

  public static Set<GoForItModifier> findGoForItModifiers(Game pGame) {
    Set<GoForItModifier> goForItModifiers = new HashSet<GoForItModifier>();
    if (Weather.BLIZZARD == pGame.getFieldModel().getWeather()) {
      goForItModifiers.add(BLIZZARD);
    }
    return goForItModifiers;
  }
  
  public static GoForItModifier[] toArray(Set<GoForItModifier> pGoForItModifierSet) {
    if (pGoForItModifierSet != null) {
      GoForItModifier[] goForItModifierArray = pGoForItModifierSet.toArray(new GoForItModifier[pGoForItModifierSet.size()]);
      Arrays.sort(
        goForItModifierArray,
        new Comparator<GoForItModifier>() {
          public int compare(GoForItModifier pO1, GoForItModifier pO2) {
            return (pO1.getId() - pO2.getId());
          }
        }
      );
      return goForItModifierArray;
    } else {
      return new GoForItModifier[0];
    }
  }
  
  public boolean isModifierIncluded() {
    return false;
  }
    
}
