package com.balancedbytes.games.ffb;



/**
 * 
 * @author Kalimar
 */
public enum CatchScatterThrowInMode {
  
  CATCH_ACCURATE_PASS(1, "catchAccuratePass", false),
  CATCH_HAND_OFF(2, "catchHandOff", false),
  CATCH_SCATTER(3, "catchScatter", false),
  SCATTER_BALL(4, "scatterBall", false),
  THROW_IN(5, "throwIn", false),
  CATCH_MISSED_PASS(6, "catchMissedPass", false),
  CATCH_KICKOFF(7, "catchKickoff", false),
  CATCH_THROW_IN(8, "catchThrowIn", false),
  FAILED_CATCH(9, "failedCatch", false),
  FAILED_PICK_UP(10, "failedPickUp", false),
  CATCH_ACCURATE_BOMB(11, "catchAccurateBomb", true),
  CATCH_BOMB(12, "catchBomb", true);
  
  private int fId;
  private String fName;
  private boolean fBomb;
  
  private CatchScatterThrowInMode(int pValue, String pName, boolean pBomb) {
    fId = pValue;
    fName = pName;
    fBomb = pBomb;
  }

  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public boolean isBomb() {
		return fBomb;
	}
  
  public static CatchScatterThrowInMode fromId(int pId) {
    for (CatchScatterThrowInMode mode : values()) {
      if (mode.getId() == pId) {
        return mode;
      }
    }
    return null;
  }
  
  public static CatchScatterThrowInMode fromName(String pName) {
    for (CatchScatterThrowInMode mode : values()) {
      if (mode.getName().equalsIgnoreCase(pName)) {
        return mode;
      }
    }
    return null;
  }
    
}
