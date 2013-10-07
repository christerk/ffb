package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class SpecialEffectFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public SpecialEffect forName(String pName) {
    for (SpecialEffect effect : SpecialEffect.values()) {
      if (effect.getName().equalsIgnoreCase(pName)) {
        return effect;
      }
    }
    return null;
  }

  public SpecialEffect forId(int pId) {
    for (SpecialEffect effect : SpecialEffect.values()) {
      if (effect.getId() == pId) {
        return effect;
      }
    }
    return null;
  }

}
