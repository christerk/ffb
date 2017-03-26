package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public interface IRollModifierFactory extends INamedObjectFactory {
  
  public IRollModifier forName(String pName);

}
