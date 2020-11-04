package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

import com.balancedbytes.games.ffb.model.Player;


/**
 * 
 * @author Kalimar
 */
public class InjuryModifierFactory implements INamedObjectFactory {
  
  public InjuryModifier forName(String pName) {
    for (InjuryModifier modifier : InjuryModifier.values()) {
      if (modifier.getName().equalsIgnoreCase(pName)) {
        return modifier;
      }
    }
    return null;
  }

  public InjuryModifier[] toArray(Set<InjuryModifier> pInjuryModifiers) {
    if (pInjuryModifiers != null) {
      InjuryModifier[] modifierArray = pInjuryModifiers.toArray(new InjuryModifier[pInjuryModifiers.size()]);
      Arrays.sort(
        modifierArray,
        new Comparator<InjuryModifier>() {
          public int compare(InjuryModifier pO1, InjuryModifier pO2) {
            return pO1.getName().compareTo(pO2.getName());
          }
        }
      );
      return modifierArray;
    } else {
      return new InjuryModifier[0];
    }
  }
  
  public InjuryModifier getNigglingInjuryModifier(Player<?> pPlayer) {
    if (pPlayer != null) {
      int nigglingInjuries = 0;
      for (SeriousInjury injury : pPlayer.getLastingInjuries()) {
        if (InjuryAttribute.NI == injury.getInjuryAttribute()) {
          nigglingInjuries++;
        }
      }
      for (InjuryModifier modifier : InjuryModifier.values()) {
        if (modifier.isNigglingInjuryModifier() && (modifier.getModifier() == nigglingInjuries)) {
          return modifier;
        }
      }
    }
    return null;
  }

}
