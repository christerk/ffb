package com.balancedbytes.games.ffb;



/**
 * 
 * @author Kalimar
 */
public enum ClientMode {
  
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
  
  public static ClientMode fromId(int pId) {
    for (ClientMode mode : values()) {
      if (mode.getId() == pId) {
        return mode;
      }
    }
    return null;
  }
  
  public static ClientMode fromName(String pName) {
    for (ClientMode mode : values()) {
      if (mode.getName().equalsIgnoreCase(pName)) {
        return mode;
      }
    }
    return null;
  }

  public static ClientMode fromArgument(String pArgument) {
    for (ClientMode mode : values()) {
      if ((mode.getArgument() != null) && mode.getArgument().equalsIgnoreCase(pArgument)) {
        return mode;
      }
    }
    return null;
  }

}
