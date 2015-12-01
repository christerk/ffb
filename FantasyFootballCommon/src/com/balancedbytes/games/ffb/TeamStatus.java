package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public enum TeamStatus implements IEnumWithName {
  
  NEW(0, "New"),
  ACTIVE(1, "Active"),
  PENDING_APPROVAL(2, "Pending Approval"),
  BLOCKED(3, "Blocked"),
  RETIRED(4, "Retired"),
  WAITING_FOR_OPPONENT(5, "Waiting for Opponent"),
  SKILL_ROLLS_PENDING(6, "Skill Rolls Pending");
  
  private int fId;
  private String fName;
  
  private TeamStatus(int pId, String pName) {
    fId = pId;
    fName = pName;
  }
  
  // TODO: this should be unnecessary
  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
}
