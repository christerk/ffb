package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

/**
 * 
 * @author Kalimar
 */
public class ArmorModifierFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public ArmorModifier forName(String pName) {
    for (ArmorModifier modifier : ArmorModifier.values()) {
      if (modifier.getName().equalsIgnoreCase(pName)) {
        return modifier;
      }
    }
    return null;
  }

  public ArmorModifier forId(int pId) {
    if (pId > 0) {
      for (ArmorModifier modifier : ArmorModifier.values()) {
        if (modifier.getId() == pId) {
          return modifier;
        }
      }
    }
    return null;
  }
  
  public ArmorModifier getFoulAssist(int pModifier) {
    for (ArmorModifier modifier : ArmorModifier.values()) {
      if (modifier.isFoulAssistModifier() && (modifier.getModifier() == pModifier)) {
        return modifier;
      }
    }
    return null;
  }
   
  public ArmorModifier[] toArray(Set<ArmorModifier> pArmorModifiers) {
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
