package com.balancedbytes.games.ffb.model.change.old;



/**
 * 
 * @author Kalimar
 */
public enum CommandActingPlayerChange {
  
  SET_PLAYER_ID(1, "setPlayerId", ModelAttributeType.STRING),
  SET_STRENGTH(2, "setStrength", ModelAttributeType.BYTE),
  SET_CURRENT_MOVE(3, "setCurrentMove", ModelAttributeType.BYTE),
  SET_GOING_FOR_IT(4, "setGoingForIt", ModelAttributeType.BOOLEAN),
  SET_DODGING(5, "setDodging", ModelAttributeType.BOOLEAN),
  SET_LEAPING(6, "setLeaping", ModelAttributeType.BOOLEAN),
  SET_HAS_BLOCKED(7, "setHasBlocked", ModelAttributeType.BOOLEAN),
  SET_HAS_FOULED(8, "setHasFouled", ModelAttributeType.BOOLEAN),
  SET_HAS_PASSED(9, "setHasPassed", ModelAttributeType.BOOLEAN),
  SET_HAS_MOVED(10, "setHasMoved", ModelAttributeType.BOOLEAN),
  SET_PLAYER_ACTION(11, "setPlayerAction", ModelAttributeType.PLAYER_ACTION),
  MARK_SKILL_USED(12, "markSkillUsed", ModelAttributeType.SKILL),
  SET_STANDING_UP(13, "setStandingUp", ModelAttributeType.BOOLEAN),
  SET_SUFFERING_BLOOD_LUST(14, "setSufferingBloodLust", ModelAttributeType.BOOLEAN),
  SET_SUFFERING_ANIMOSITY(15, "setSufferingAnimosity", ModelAttributeType.BOOLEAN),
  SET_HAS_FED(16, "setHasFed", ModelAttributeType.BOOLEAN);

  private int fId;
  private String fName;
  private ModelAttributeType fAttributeType;
  
  private CommandActingPlayerChange(int pValue, String pName, ModelAttributeType pAttributeType) {
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
  
  public static CommandActingPlayerChange fromId(int pId) {
    for (CommandActingPlayerChange command : values()) {
      if (command.getId() == pId) {
        return command;
      }
    }
    return null;
  }
    
  public static CommandActingPlayerChange fromName(String pName) {
    for (CommandActingPlayerChange command : values()) {
      if (command.getName().equalsIgnoreCase(pName)) {
        return command;
      }
    }
    return null;
  }

}
