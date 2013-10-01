package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum InducementPhase {
  
  END_OF_OWN_TURN(1, "endOfOwnTurn", "at end of own turn"),
  START_OF_OWN_TURN(2, "startOfOwnTurn","at start of own turn"), 
  AFTER_KICKOFF_TO_OPPONENT_RESOLVED(3, "afterKickoffToOpponentResolved", "after Kickoff to opponent resolved"),
  AFTER_INDUCEMENTS_PURCHASED(4, "afterInducementsPurchased", "after Inducements are purchased"),
  BEFORE_KICKOFF_SCATTER(5, "beforeKickoffScatter", "before Kickoff Scatter"),
  END_OF_TURN_NOT_HALF(6, "endOfTurnNotHalf", "at end of turn, not half"),
  BEFORE_SETUP(7, "beforeSetup", "before setting up");

  private int fId;
  private String fName;
  private String fDescription;
  
  private InducementPhase(int pValue, String pName, String pDescription) {
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
  
  public static InducementPhase fromId(int pId) {
    for (InducementPhase type : values()) {
      if (type.getId() == pId) {
        return type;
      }
    }
    return null;
  }
    
  public static InducementPhase fromName(String pName) {
    for (InducementPhase type : values()) {
      if (type.getName().equalsIgnoreCase(pName)) {
        return type;
      }
    }
    return null;
  }
  
  public static String getDescription(InducementPhase[] pPhases) {
  	StringBuilder description = new StringBuilder();
  	description.append("Play ");
  	boolean firstPhase = true;
  	for (InducementPhase phase : pPhases) {
  		if (firstPhase) {
  			firstPhase = false;
  		} else {
  			description.append(" or ");
  		}
  		description.append(phase.getDescription());
  	}
  	return description.toString();
  }
  
}
