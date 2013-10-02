package com.balancedbytes.games.ffb;



/**
 * 
 * @author Kalimar
 */
public enum ConcedeGameStatus implements IEnumWithId, IEnumWithName {
  
  REQUESTED(1, "requested"),
  CONFIRMED(2, "confirmed"),
  DENIED(3, "denied");
  
  private int fId;
  private String fName;
  
  private ConcedeGameStatus(int pValue, String pName) {
    fId = pValue;
    fName = pName;
  }

  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
      
}
