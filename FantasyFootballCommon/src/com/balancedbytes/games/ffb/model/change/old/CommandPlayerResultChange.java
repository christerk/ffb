package com.balancedbytes.games.ffb.model.change.old;




/**
 * 
 * @author Kalimar
 */
public enum CommandPlayerResultChange {
  
  SET_SERIOUS_INJURY(1, "setSeriousInjury", ModelAttributeType.SERIOUS_INJURY),
  SET_SEND_TO_BOX_REASON(2, "setSendToBoxReason", ModelAttributeType.SEND_TO_BOX_REASON),
  SET_SEND_TO_BOX_TURN(3, "setSendToBoxTurn", ModelAttributeType.BYTE),
  SET_SEND_TO_BOX_HALF(4, "setSendToBoxHalf", ModelAttributeType.BYTE),
  SET_SEND_TO_BOX_BY_PLAYER_ID(5, "setSendToBoxByPlayerId", ModelAttributeType.STRING),
  SET_COMPLETIONS(6, "setCompletions", ModelAttributeType.BYTE),
  SET_TOUCHDOWNS(7, "setTouchdowns", ModelAttributeType.BYTE),
  SET_INTERCEPTIONS(8, "setInterceptions", ModelAttributeType.BYTE),
  SET_CASUALTIES(9, "setCasualties", ModelAttributeType.BYTE),
  SET_PLAYER_AWARDS(10, "setPlayerAwards", ModelAttributeType.BYTE),
  SET_BLOCKS(11, "setBlock", ModelAttributeType.BYTE),
  SET_FOULS(12, "setFouls", ModelAttributeType.BYTE),
  SET_TURNS_PLAYED(13, "setTurnsPlayed", ModelAttributeType.BYTE),
  SET_RUSHING(14, "setRushing", ModelAttributeType.INTEGER),
  SET_PASSING(15, "setPassing", ModelAttributeType.INTEGER),
  SET_CURRENT_SPPS(16, "setCurrentSpps", ModelAttributeType.INTEGER),
  SET_DEFECTING(17, "setDefecting", ModelAttributeType.BOOLEAN),
  SET_SERIOUS_INJURY_DECAY(18, "setSeriousInjuryDecay", ModelAttributeType.SERIOUS_INJURY),
  SET_HAS_USED_SECRET_WEAPON(19, "setHasUsedSecretWeapon", ModelAttributeType.BOOLEAN);

  private int fId;
  private String fName;
  private ModelAttributeType fAttributeType;
  
  private CommandPlayerResultChange(int pValue, String pName, ModelAttributeType pAttributeType) {
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
  
  public static CommandPlayerResultChange fromId(int pId) {
    for (CommandPlayerResultChange command : values()) {
      if (command.getId() == pId) {
        return command;
      }
    }
    return null;
  }
    
  public static CommandPlayerResultChange fromName(String pName) {
    for (CommandPlayerResultChange command : values()) {
      if (command.getName().equalsIgnoreCase(pName)) {
        return command;
      }
    }
    return null;
  }

}
