package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public interface IRollModifier extends IEnumWithId, IEnumWithName {
  
  public int getModifier();
  
  public boolean isModifierIncluded();

}
