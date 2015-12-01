package com.balancedbytes.games.ffb.net;

import com.balancedbytes.games.ffb.IEnumWithName;



/**
 * 
 * @author Kalimar
 */
public enum ServerStatus implements IEnumWithName {
  
  ERROR_UNKNOWN_COACH("Unknown Coach", "Unknown Coach!"),
  ERROR_WRONG_PASSWORD("Wrong Password", "Wrong Password!"),
  ERROR_GAME_IN_USE("Game In Use", "A Game with this name is already in use!"),
  ERROR_ALREADY_LOGGED_IN("Already Logged In", "You are already logged in!"),
  ERROR_NOT_YOUR_TEAM("Not Your Team", "The team you wanted to join with is not yours!"),
  ERROR_UNKNOWN_GAME_ID("Unknown Game Id", "There is no game with the given id!"),
  ERROR_SAME_TEAM("Same Team", "You cannot play a team against itself!"),
  FUMBBL_ERROR("Fumbbl Error", "Fumbbl Error");
  
  private String fName;
  private String fMessage;
  
  private ServerStatus(String pName, String pMessage) {
    fName = pName;
    fMessage = pMessage;
  }
  
  public String getName() {
    return fName;
  }
  
  public String getMessage() {
    return fMessage;
  }
  
}
