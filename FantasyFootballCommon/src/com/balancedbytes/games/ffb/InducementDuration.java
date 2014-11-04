package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum InducementDuration {
	
  UNTIL_END_OF_GAME(1, "untilEndOfGame", "For the entire game"),
  UNTIL_END_OF_DRIVE(2, "untilEndOfDrive", "For this drive"),
  UNTIL_END_OF_TURN(3, "untilEndOfTurn", "For this turn"),
  WHILE_HOLDING_THE_BALL(4, "whileHoldingTheBall", "While holding the ball"),
  UNTIL_USED(5, "untilUsed", "Single use"),
  UNTIL_END_OF_OPPONENTS_TURN(6, "untilEndOfOpponentsTurn", "For opponent's turn");

  
  private int fId;
  private String fName;
  private String fDescription;
  
  private InducementDuration(int pValue, String pName, String pDescription) {
    fId = pValue;
    fName = pName;
    fDescription = pDescription;
  }

  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public String getDescription() {
	  return fDescription;
  }
  
  public static InducementDuration fromId(int pId) {
    for (InducementDuration type : values()) {
      if (type.getId() == pId) {
        return type;
      }
    }
    return null;
  }
    
  public static InducementDuration fromName(String pName) {
    for (InducementDuration type : values()) {
      if (type.getName().equalsIgnoreCase(pName)) {
        return type;
      }
    }
    return null;
  }
  
}
