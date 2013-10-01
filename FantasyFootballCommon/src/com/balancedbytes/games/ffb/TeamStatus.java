package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public enum TeamStatus implements IEnumWithId, IEnumWithName {
  
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
  
  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public static TeamStatus fromId(int pId) {
    for (TeamStatus status : values()) {
      if (status.getId() == pId) {
        return status;
      }
    }
    return null;
  }

}
