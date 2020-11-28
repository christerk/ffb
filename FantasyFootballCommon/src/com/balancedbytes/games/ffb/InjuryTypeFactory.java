package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class InjuryTypeFactory implements INamedObjectFactory {
	
  public InjuryType forName(String pName) {
	  return InjuryTypes.values().get(pName.toLowerCase());
  }
}

