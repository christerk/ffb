package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

import com.balancedbytes.games.ffb.model.Player;

/**
 * 
 * @author Kalimar
 */
public enum InjuryModifier {
  
  MIGHTY_BLOW(1, "Mighty Blow", 1, false),
  DIRTY_PLAYER(2, "Dirty Player", 1, false),
  STUNTY(3, "Stunty", 0, false),
  THICK_SKULL(4, "Thick Skull", 0, false),
  NIGGLING_INJURIES_1(5, "1 Niggling Injury", 1, true),
  NIGGLING_INJURIES_2(6, "2 Niggling Injuries", 2, true),
  NIGGLING_INJURIES_3(7, "3 Niggling Injuries", 3, true),
  NIGGLING_INJURIES_4(8, "4 Niggling Injuries", 4, true),
  NIGGLING_INJURIES_5(9, "5 Niggling Injuries", 5, true);

  
  private int fId;
  private String fName;
  private int fModifier;
  private boolean fNigglingInjuryModifier;
  
  private InjuryModifier(int pId, String pName, int pModifier, boolean pNigglingInjuryModifier) {
    fId = pId;
    fName = pName;
    fModifier = pModifier;
    fNigglingInjuryModifier = pNigglingInjuryModifier;
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
  
  public boolean isNigglingInjuryModifier() {
    return fNigglingInjuryModifier;
  }
  
  public static InjuryModifier fromId(int pId) {
    for (InjuryModifier modifier : values()) {
      if (modifier.getId() == pId) {
        return modifier;
      }
    }
    return null;
  }
  
  public static InjuryModifier fromName(String pName) {
    for (InjuryModifier modifier : values()) {
      if (modifier.getName().equalsIgnoreCase(pName)) {
        return modifier;
      }
    }
    return null;
  }

  public static InjuryModifier[] toArray(Set<InjuryModifier> pInjuryModifiers) {
    if (pInjuryModifiers != null) {
      InjuryModifier[] modifierArray = pInjuryModifiers.toArray(new InjuryModifier[pInjuryModifiers.size()]);
      Arrays.sort(
        modifierArray,
        new Comparator<InjuryModifier>() {
          public int compare(InjuryModifier pO1, InjuryModifier pO2) {
            return (pO1.getId() - pO2.getId());
          }
        }
      );
      return modifierArray;
    } else {
      return new InjuryModifier[0];
    }
  }
  
  public static InjuryModifier getNigglingInjuryModifier(Player pPlayer) {
    if (pPlayer != null) {
      int nigglingInjuries = 0;
      for (SeriousInjury injury : pPlayer.getLastingInjuries()) {
        if (InjuryAttribute.NI == injury.getInjuryAttribute()) {
          nigglingInjuries++;
        }
      }
      for (InjuryModifier modifier : values()) {
        if (modifier.isNigglingInjuryModifier() && (modifier.getModifier() == nigglingInjuries)) {
          return modifier;
        }
      }
    }
    return null;
  }
    
}
