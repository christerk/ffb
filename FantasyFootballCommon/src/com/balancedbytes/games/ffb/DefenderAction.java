package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public enum DefenderAction {
  
  DUMP_OFF(1, "dumpOff", "Dump Off", "dump off the ball");
  
  private int fId;
  private String fName;
  private String fTitle;
  private String fDescription;
  
  private DefenderAction(int pId, String pName, String pTitle, String pDescription) {
    fId = pId;
    fName = pName;
    fTitle = pTitle;
    fDescription = pDescription;
  }
  
  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public String getTitle() {
    return fTitle;
  }
  
  public String getDescription() {
    return fDescription;
  }
  
  public static DefenderAction fromId(int pValue) {
    for (DefenderAction action : values()) {
      if (action.getId() == pValue) {
        return action;
      }
    }
    return null;
  }

  public static DefenderAction fromName(String pName) {
    for (DefenderAction action : values()) {
      if (action.getName().equalsIgnoreCase(pName)) {
        return action;
      }
    }
    return null;
  }

}
