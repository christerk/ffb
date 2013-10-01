package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

/**
 * 
 * @author Kalimar
 */
public enum ArmorModifier {
  
  CLAWS(1, "Claws", 0, false),
  MIGHTY_BLOW(2, "Mighty Blow", 1, false),
  FOUL_PLUS_1(3, "1 Offensive Assist", 1, true),
  FOUL_PLUS_2(4, "2 Offensive Assists", 2, true),
  FOUL_PLUS_3(5, "3 Offensive Assists", 3, true),
  FOUL_PLUS_4(6, "4 Offensive Assists", 4, true),
  FOUL_PLUS_5(7, "5 Offensive Assists", 5, true),
  FOUL_PLUS_6(8, "6 Offensive Assists", 6, true),
  FOUL_PLUS_7(9, "7 Offensive Assists", 7, true),
  FOUL_MINUS_1(10, "1 Defensive Assist", -1, true),
  FOUL_MINUS_2(11, "2 Defensive Assists", -2, true),
  FOUL_MINUS_3(12, "3 Defensive Assists", -3, true),
  FOUL_MINUS_4(13, "4 Defensive Assists", -4, true),
  FOUL_MINUS_5(14, "5 Defensive Assists", -5, true),
  DIRTY_PLAYER(15, "Dirty Player", 1, false),
  STAKES(16, "Stakes", 1, false),
  CHAINSAW(17, "Chainsaw", 3, false),
  FOUL(18, "Foul", 1, false);
  
  private int fId;
  private String fName;
  private int fModifier;
  private boolean fFoulAssistModifier; 
  
  private ArmorModifier(int pId, String pName, int pModifier, boolean pFoulAssistModifier) {
    fId = pId;
    fName = pName;
    fModifier = pModifier;
    fFoulAssistModifier = pFoulAssistModifier;
  }
  
  public int getModifier() {
    return fModifier;
  }
  
  public String getName() {
    return fName;
  }
  
  public int getId() {
    return fId;
  }
  
  public boolean isFoulAssistModifier() {
    return fFoulAssistModifier;
  }
  
  public static ArmorModifier getFoulAssist(int pModifier) {
    for (ArmorModifier modifier : values()) {
      if (modifier.isFoulAssistModifier() && (modifier.getModifier() == pModifier)) {
        return modifier;
      }
    }
    return null;
  }
  
  public static ArmorModifier fromId(int pId) {
    for (ArmorModifier modifier : values()) {
      if (modifier.getId() == pId) {
        return modifier;
      }
    }
    return null;
  }
  
  public static ArmorModifier fromName(String pName) {
    for (ArmorModifier modifier : values()) {
      if (modifier.getName().equalsIgnoreCase(pName)) {
        return modifier;
      }
    }
    return null;
  }
  
  public static ArmorModifier[] toArray(Set<ArmorModifier> pArmorModifiers) {
    if (pArmorModifiers != null) {
      ArmorModifier[] modifierArray = pArmorModifiers.toArray(new ArmorModifier[pArmorModifiers.size()]);
      Arrays.sort(
        modifierArray,
        new Comparator<ArmorModifier>() {
          public int compare(ArmorModifier pO1, ArmorModifier pO2) {
            return (pO1.getId() - pO2.getId());
          }
        }
      );
      return modifierArray;
    } else {
      return new ArmorModifier[0];
    }
  }
  
}
