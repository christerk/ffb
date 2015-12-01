package com.balancedbytes.games.ffb;



/**
 * 
 * @author Kalimar
 */
public enum ConcedeGameStatus implements IEnumWithName {
  
  REQUESTED("requested"),
  CONFIRMED("confirmed"),
  DENIED("denied");
  
  private String fName;
  
  private ConcedeGameStatus(String pName) {
    fName = pName;
  }

  public String getName() {
    return fName;
  }
      
}
