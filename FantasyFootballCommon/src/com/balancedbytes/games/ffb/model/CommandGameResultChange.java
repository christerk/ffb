package com.balancedbytes.games.ffb.model;



/**
 * 
 * @author Kalimar
 */
public enum CommandGameResultChange {
  
  SET_TURN_NR(1, "setTurnNr", ModelAttributeType.BYTE),
  SET_RE_ROLLS(2, "setReRolls", ModelAttributeType.BYTE),
  SET_APOTHECARIES(3, "setApothecaries", ModelAttributeType.BYTE),
  SET_BLITZ_USED(4, "setBlitzUsed", ModelAttributeType.BOOLEAN),
  SET_FOUL_USED(5, "setFoulUsed", ModelAttributeType.BOOLEAN),
  SET_RE_ROLL_USED(6, "setReRollUsed", ModelAttributeType.BOOLEAN),
  SET_HAND_OVER_USED(7, "setHandOverUsed", ModelAttributeType.BOOLEAN),
  SET_PASS_USED(8, "setPassUsed", ModelAttributeType.BOOLEAN);

  private int fId;
  private String fName;
  private ModelAttributeType fAttributeType;
  
  private CommandGameResultChange(int pValue, String pName, ModelAttributeType pAttributeType) {
    fId = pValue;
    fName = pName;
    fAttributeType = pAttributeType;
  }

  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public ModelAttributeType getAttributeType() {
    return fAttributeType;
  }
  
  public static CommandGameResultChange fromId(int pId) {
    for (CommandGameResultChange command : values()) {
      if (command.getId() == pId) {
        return command;
      }
    }
    return null;
  }
    
  public static CommandGameResultChange fromName(String pName) {
    for (CommandGameResultChange command : values()) {
      if (command.getName().equalsIgnoreCase(pName)) {
        return command;
      }
    }
    return null;
  }

}
