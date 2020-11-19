package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.server.InjuryTypes;

/**
 * 
 * @author Kalimar
 */
public class InjuryTypeFactory implements INamedObjectFactory {
	
  public InjuryType forName(String pName) {
	  return InjuryTypes.values().get(pName.toLowerCase());
  }
}

