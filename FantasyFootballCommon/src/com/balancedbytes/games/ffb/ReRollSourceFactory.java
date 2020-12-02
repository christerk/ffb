package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class ReRollSourceFactory implements INamedObjectFactory {
  
  public ReRollSource forName(String pName) {
	 return ReRollSources.values().get(pName.toLowerCase());
  }

}
