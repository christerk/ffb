package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum CardTarget {
  
  TURN(1, "turn"),
  OWN_PLAYER(2, "ownPlayer"), 
  OPPOSING_PLAYER(3, "opposingPlayer");

  private int fId;
  private String fName;
  
  private CardTarget(int pValue, String pName) {
    fId = pValue;
    fName = pName;
  }

  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public boolean isPlayedOnPlayer() {
	  return ((this == OWN_PLAYER) || (this == OPPOSING_PLAYER));
  }
  
  public static CardTarget fromId(int pId) {
    for (CardTarget target : values()) {
      if (target.getId() == pId) {
        return target;
      }
    }
    return null;
  }
    
  public static CardTarget fromName(String pName) {
    for (CardTarget target : values()) {
      if (target.getName().equalsIgnoreCase(pName)) {
        return target;
      }
    }
    return null;
  }
  
}
