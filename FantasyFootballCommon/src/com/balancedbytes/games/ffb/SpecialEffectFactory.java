package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class SpecialEffectFactory implements INamedObjectFactory {
  
  public SpecialEffect forName(String pName) {
    for (SpecialEffect effect : SpecialEffect.values()) {
      if (effect.getName().equalsIgnoreCase(pName)) {
        return effect;
      }
    }
    return null;
  }
  
}
