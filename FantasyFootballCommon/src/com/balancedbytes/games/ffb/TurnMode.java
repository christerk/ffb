package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum TurnMode implements IEnumWithName {
  
  REGULAR("regular"),
  SETUP("setup"),
  KICKOFF("kickoff"),
  PERFECT_DEFENCE("perfectDefence"),
  QUICK_SNAP("quickSnap"),
  HIGH_KICK("highKick"),
  START_GAME("startGame"),
  BLITZ("blitz"),
  TOUCHBACK("touchback"),
  INTERCEPTION("interception"),
  END_GAME("endGame"),
  KICKOFF_RETURN("kickoffReturn"),
  WIZARD("wizard"),
  PASS_BLOCK("passBlock"),
  DUMP_OFF("dumpOff"),
  NO_PLAYERS_TO_FIELD("noPlayersToField"),
  BOMB_HOME("bombHome"),
  BOMB_AWAY("bombAway"),
  BOMB_HOME_BLITZ("bombHomeBlitz"),
  BOMB_AWAY_BLITZ("bombAwayBlitz"),
  ILLEGAL_SUBSTITUTION("illegalSubstitution");
  
  private String fName;
  
  private TurnMode(String pName) {
    fName = pName;
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

}
