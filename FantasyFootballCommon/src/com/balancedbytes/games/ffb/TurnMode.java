package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum TurnMode {
  
  REGULAR(1, "regular"),
  SETUP(2, "setup"),
  KICKOFF(3, "kickoff"),
  PERFECT_DEFENCE(4, "perfectDefence"),
  QUICK_SNAP(5, "quickSnap"),
  HIGH_KICK(6, "highKick"),
  START_GAME(7, "startGame"),
  BLITZ(8, "blitz"),
  TOUCHBACK(9, "touchback"),
  INTERCEPTION(10, "interception"),
  END_GAME(11, "endGame"),
  KICKOFF_RETURN(12, "kickoffReturn"),
  WIZARD(13, "wizard"),
  PASS_BLOCK(14, "passBlock"),
  DUMP_OFF(15, "dumpOff"),
  NO_PLAYERS_TO_FIELD(16, "noPlayersToField"),
  BOMB_HOME(17, "bombHome"),
  BOMB_AWAY(18, "bombAway"),
  BOMB_HOME_BLITZ(19, "bombHomeBlitz"),
  BOMB_AWAY_BLITZ(20, "bombAwayBlitz");
  
  private int fId;
  private String fName;
  
  private TurnMode(int pValue, String pName) {
    fId = pValue;
    fName = pName;
  }

  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public boolean checkNegatraits() {
  	return ((this != KICKOFF_RETURN) && (this != PASS_BLOCK) && !isBombTurn());
  }
  
  public boolean isBombTurn() {
  	return ((this == BOMB_HOME) || (this == BOMB_HOME_BLITZ) || (this == BOMB_AWAY) || (this == BOMB_AWAY_BLITZ));
  }
  
  public static TurnMode fromId(int pId) {
    for (TurnMode mode : values()) {
      if (mode.getId() == pId) {
        return mode;
      }
    }
    return null;
  }
    
  public static TurnMode fromName(String pName) {
    for (TurnMode mode : values()) {
      if (mode.getName().equalsIgnoreCase(pName)) {
        return mode;
      }
    }
    return null;
  }

}
