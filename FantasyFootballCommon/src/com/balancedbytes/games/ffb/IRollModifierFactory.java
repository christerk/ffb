package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public interface IRollModifierFactory extends IEnumWithIdFactory, IEnumWithNameFactory {
  
  public IRollModifier forId(int pId);
  
  public IRollModifier forName(String pName);

}
