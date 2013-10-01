package com.balancedbytes.games.ffb.model;



/**
 * 
 * @author Kalimar
 */
public enum CommandTeamResultChange {
  
  SET_SCORE(1, "setScore", ModelAttributeType.BYTE),
  SET_CONCEDED(2, "setConceded", ModelAttributeType.BOOLEAN),
  SET_SPECTATORS(3, "setSpectators", ModelAttributeType.INTEGER),
  SET_FAME(4, "setFame", ModelAttributeType.BYTE),
  SET_WINNINGS(5, "setWinnings", ModelAttributeType.INTEGER),
  SET_FAN_FACTOR_MODIFIER(6, "setFanFactorModifier", ModelAttributeType.BYTE),
  SET_BADLY_HURT_SUFFERED(7, "setBadlyHurtSuffered", ModelAttributeType.BYTE),
  SET_SERIOUS_INJURY_SUFFERED(8, "setSeriousInjurySuffered", ModelAttributeType.BYTE),
  SET_RIP_SUFFERED(9, "setRipSuffered", ModelAttributeType.BYTE),
  SET_SPIRALLING_EXPENSES(10, "setSpirallingExpenses", ModelAttributeType.INTEGER),
  SET_RAISED_DEAD(11, "setRaisedDead", ModelAttributeType.INTEGER),
  SET_PETTY_CASH_TRANSFERRED(12, "setPettyCashTransferred", ModelAttributeType.INTEGER),
  SET_PETTY_CASH_USED(13, "setPettyCashUsed", ModelAttributeType.INTEGER),
  SET_TEAM_VALUE(14, "setTeamValue", ModelAttributeType.INTEGER);

  private int fId;
  private String fName;
  private ModelAttributeType fAttributeType;
  
  private CommandTeamResultChange(int pValue, String pName, ModelAttributeType pAttributeType) {
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
  
  public static CommandTeamResultChange fromId(int pId) {
    for (CommandTeamResultChange command : values()) {
      if (command.getId() == pId) {
        return command;
      }
    }
    return null;
  }
    
  public static CommandTeamResultChange fromName(String pName) {
    for (CommandTeamResultChange command : values()) {
      if (command.getName().equalsIgnoreCase(pName)) {
        return command;
      }
    }
    return null;
  }

}
