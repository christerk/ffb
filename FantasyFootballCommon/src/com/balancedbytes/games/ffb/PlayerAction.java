package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public enum PlayerAction {
  
  MOVE(1, "move", 1, "starts a Move Action"),
  BLOCK(2, "block", 2, "starts a Block Action"),
  BLITZ(3, "blitz", 3, null),
  BLITZ_MOVE(4, "blitzMove", 3, "starts a Blitz Action"),
  HAND_OVER(5, "handOver", 5, null),
  HAND_OVER_MOVE(6, "handOverMove", 5, "starts a Hand Over Action"),
  PASS(7, "pass", 7, null),
  PASS_MOVE(8, "passMove", 7, "starts a Pass Action"),
  FOUL(9, "foul", 9, null),
  FOUL_MOVE(10, "foulMove", 9, "starts a Foul Action"),
  STAND_UP(11, "standUp", 11, "stands up"),
  THROW_TEAM_MATE(12, "throwTeamMate", 12, null),
  THROW_TEAM_MATE_MOVE(13, "throwTeamMateMove", 12, null),
  REMOVE_CONFUSION(14, "removeConfusion", 14, null),
  GAZE(15, "gaze", 15, null),
  MULTIPLE_BLOCK(16, "multipleBlock", 16, "starts a Block Action"),
  HAIL_MARY_PASS(17, "hailMaryPass", 7, null),
  DUMP_OFF(18, "dumpOff", 7, null),
  STAND_UP_BLITZ(19, "standUpBlitz", 3, "stands up with Blitz"),
  THROW_BOMB(20, "throwBomb", 20, "starts a Bomb Action"),
  HAIL_MARY_BOMB(21, "hailMaryBomb", 21, null);
  
  private int fId;
  private String fName;
  private int fType;
  private String fDescription;
  
  private PlayerAction(int pId, String pName, int pType, String pDescription) {
    fId = pId;
    fName = pName;
    fType = pType;
    fDescription = pDescription;
  }
  
  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public int getType() {
    return fType;
  }
  
  public String getDescription() {
    return fDescription;
  }
  
  public boolean isMoving() {
    return ((this == MOVE) || (this == BLITZ_MOVE) || (this == HAND_OVER_MOVE) || (this == PASS_MOVE) || (this == FOUL_MOVE) || (this == THROW_TEAM_MATE_MOVE));
  }
  
  public boolean isPassing() {
  	return ((this == PASS) || (this == DUMP_OFF) || (this == HAND_OVER) || (this == HAIL_MARY_PASS) || (this == THROW_BOMB) || (this == HAIL_MARY_BOMB));
  }
  
  public static PlayerAction fromId(int pValue) {
    for (PlayerAction action : values()) {
      if (action.getId() == pValue) {
        return action;
      }
    }
    return null;
  }

  public static PlayerAction fromName(String pName) {
    for (PlayerAction action : values()) {
      if (action.getName().equalsIgnoreCase(pName)) {
        return action;
      }
    }
    return null;
  }
  
}
