package com.balancedbytes.games.ffb.net;



/**
 * 
 * @author Kalimar
 */
public enum ServerStatus {
  
  ERROR_UNKNOWN_COACH(1, "Unknown Coach", "Unknown Coach!"),
  ERROR_WRONG_PASSWORD(2, "Wrong Password", "Wrong Password!"),
  ERROR_GAME_IN_USE(3, "Game In Use", "A Game with this name is already in use!"),
  ERROR_ALREADY_LOGGED_IN(4, "Already Logged In", "You are already logged in!"),
  ERROR_NOT_YOUR_TEAM(5, "Not Your Team", "The team you wanted to join with is not yours!"),
  ERROR_UNKNOWN_GAME_ID(6, "Unknown Game Id", "There is no game with the given id!"),
  ERROR_SAME_TEAM(7, "Same Team", "You cannot play a team against itself!"),
  FUMBBL_ERROR(8, "Fumbbl Error", "Fumbbl Error");
  
  private int fId;
  private String fName;
  private String fMessage;
  
  private ServerStatus(int pId, String pName, String pMessage) {
    fId = pId;
    fName = pName;
    fMessage = pMessage;
  }
  
  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public String getMessage() {
    return fMessage;
  }
  
  public static ServerStatus fromId(int pId) {
    if (pId > 0) {
      for (ServerStatus serverStatus : values()) {
        if (pId == serverStatus.getId()) {
          return serverStatus;
        }
      }
    }
    return null;
  }

  public static ServerStatus fromName(String pName) {
    for (ServerStatus serverStatus : values()) {
      if (serverStatus.getName().equalsIgnoreCase(pName)) {
        return serverStatus;
      }
    }
    return null;
  }

}
