package com.balancedbytes.games.ffb;



/**
 * 
 * @author Kalimar
 */
public enum ClientMode implements INamedObject {
  
  PLAYER("player", "-player"),
  SPECTATOR("spectator", "-spectator"),
  REPLAY("replay", "-replay");
  
  private String fName;
  private String fArgument;
  
  private ClientMode(String pName, String pArgument) {
    fName = pName;
    fArgument = pArgument;
  }

  public String getName() {
    return fName;
  }
  
  public String getArgument() {
		return fArgument;
	}

}
