package com.balancedbytes.games.ffb;



/**
 * 
 * @author Kalimar
 */
public enum ClientMode implements IEnumWithId, IEnumWithName {
  
  PLAYER(1, "player", "-player"),
  SPECTATOR(2, "spectator", "-spectator"),
  REPLAY(3, "replay", "-replay");
  
  private int fId;
  private String fName;
  private String fArgument;
  
  private ClientMode(int pValue, String pName, String pArgument) {
    fId = pValue;
    fName = pName;
    fArgument = pArgument;
  }

  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public String getArgument() {
		return fArgument;
	}

}
