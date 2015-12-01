package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public interface IRollModifierFactory extends IEnumWithNameFactory {
  
  public IRollModifier forName(String pName);

}
