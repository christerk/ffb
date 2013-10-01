package com.balancedbytes.games.ffb.model;



/**
 * 
 * @author Kalimar
 */
public enum CommandTurnDataChange {
  
  SET_TURN_NR(1, "setTurnNr", ModelAttributeType.BYTE),
  SET_RE_ROLLS(2, "setReRolls", ModelAttributeType.BYTE),
  SET_APOTHECARIES(3, "setApothecaries", ModelAttributeType.BYTE),
  SET_BLITZ_USED(4, "setBlitzUsed", ModelAttributeType.BOOLEAN),
  SET_FOUL_USED(5, "setFoulUsed", ModelAttributeType.BOOLEAN),
  SET_RE_ROLL_USED(6, "setReRollUsed", ModelAttributeType.BOOLEAN),
  SET_HAND_OVER_USED(7, "setHandOverUsed", ModelAttributeType.BOOLEAN),
  SET_PASS_USED(8, "setPassUsed", ModelAttributeType.BOOLEAN),
  ADD_INDUCEMENT(9, "addInducement", ModelAttributeType.INDUCEMENT),
  REMOVE_INDUCEMENT(10, "removeInducement", ModelAttributeType.INDUCEMENT),
  SET_FIRST_TURN_AFTER_KICKOFF(11, "setFirstTurnAfterKickoff", ModelAttributeType.BOOLEAN),
  SET_TURN_STARTED(12, "setTurnStarted", ModelAttributeType.BOOLEAN),
  ADD_AVAILABLE_CARD(13, "addAvailableCard", ModelAttributeType.CARD),
  REMOVE_AVAILABLE_CARD(14, "removeAvailableCard", ModelAttributeType.CARD),
  ACTIVATE_CARD(15, "activateCard", ModelAttributeType.CARD),
  DEACTIVATE_CARD(16, "deactivateCard", ModelAttributeType.CARD);
  
  private int fId;
  private String fName;
  private ModelAttributeType fAttributeType;
  
  private CommandTurnDataChange(int pValue, String pName, ModelAttributeType pAttributeType) {
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
  
  public static CommandTurnDataChange fromId(int pId) {
    for (CommandTurnDataChange command : values()) {
      if (command.getId() == pId) {
        return command;
      }
    }
    return null;
  }
    
  public static CommandTurnDataChange fromName(String pName) {
    for (CommandTurnDataChange command : values()) {
      if (command.getName().equalsIgnoreCase(pName)) {
        return command;
      }
    }
    return null;
  }

}