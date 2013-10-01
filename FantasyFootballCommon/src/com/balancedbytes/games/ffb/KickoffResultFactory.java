package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class KickoffResultFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public KickoffResult forName(String pName) {
    for (KickoffResult kickoff : KickoffResult.values()) {
      if (kickoff.getName().equalsIgnoreCase(pName)) {
        return kickoff;
      }
    }
    return null;
  }

  public KickoffResult forId(int pId) {
    for (KickoffResult kickoff : KickoffResult.values()) {
      if (kickoff.getId() == pId) {
        return kickoff;
      }
    }
    return null;
  }
  
}
