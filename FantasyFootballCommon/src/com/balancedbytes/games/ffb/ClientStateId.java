package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum ClientStateId implements IEnumWithId, IEnumWithName {

  LOGIN(1, "login"),
  RE_ROLL(2, "reRoll"),
  START_GAME(3, "startGame"),
  SELECT_PLAYER(4, "selectPlayer"),
  MOVE(5, "move"),
  BLOCK(6, "block"),
  BLITZ(7, "blitz"),
  HAND_OVER(8, "handOver"),
  PASS(9, "pass"),
  SPECTATE(10, "spectate"),
  SETUP(11, "setup"),
  KICKOFF(12, "kickoff"),
  PUSHBACK(13, "pushback"),
  INTERCEPTION(14, "interception"),
  FOUL(15, "foul"),
  HIGH_KICK(16, "highKick"),
  QUICK_SNAP(17, "quickSnap"),
  TOUCHBACK(18, "touchback"),
  WAIT_FOR_OPPONENT(19, "waitForOpponent"),
  REPLAY(20, "replay"),
  THROW_TEAM_MATE(21, "throwTeamMate"),
  DUMP_OFF(22, "dumpOff"),
  WAIT_FOR_SETUP(23, "waitForSetup"),
  GAZE(24, "gaze"),
  KICKOFF_RETURN(25, "kickoffReturn"),
  WIZARD(26, "wizard"),
  PASS_BLOCK(27, "passBlock"),
  BOMB(28, "bomb");
  
  private int fId;
  private String fName;
  
  private ClientStateId(int pId, String pName) {
    fId = pId;
    fName = pName;
  }
  
  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public String toString() {
    return getName();
  }

}
