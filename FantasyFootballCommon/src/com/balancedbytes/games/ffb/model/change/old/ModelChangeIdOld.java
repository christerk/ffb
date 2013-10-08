package com.balancedbytes.games.ffb.model.change.old;



/**
 * 
 * @author Kalimar
 */
public enum ModelChangeIdOld {
  
  ACTING_PLAYER_CHANGE(1, "actingPlayerChange"),
  TURN_DATA_CHANGE(2, "turnDataChange"),
  GAME_ATTRIBUTE_CHANGE(3, "gameAttributeChange"),
  TEAM_RESULT_CHANGE(4, "teamResultChange"),
  PLAYER_RESULT_CHANGE(5, "playerResultChange"),
  FIELD_MODEL_CHANGE(6, "fieldModelChange");
  
  private int fId;
  private String fName;
  
  private ModelChangeIdOld(int pId, String pName) {
    fId = pId;
    fName = pName;
  }
  
  public int getId() {
    return fId;
  }

  public String getName() {
    return fName;
  }
  
  public static ModelChangeIdOld fromId(int pValue) {
    for (ModelChangeIdOld mode : values()) {
      if (mode.getId() == pValue) {
        return mode;
      }
    }
    return null;
  }
  
  public static ModelChangeIdOld fromName(String pName) {
    for (ModelChangeIdOld mode : values()) {
      if (mode.getName().equalsIgnoreCase(pName)) {
        return mode;
      }
    }
    return null;
  }
  
  public IModelChange createModelChange() {
    switch (this) {
      case ACTING_PLAYER_CHANGE:
        return new ModelChangeActingPlayer();
      case TURN_DATA_CHANGE:
        return new ModelChangeTurnData();
      case GAME_ATTRIBUTE_CHANGE:
        return new ModelChangeGameAttribute();
      case TEAM_RESULT_CHANGE:
        return new ModelChangeTeamResult();
      case PLAYER_RESULT_CHANGE:
        return new ModelChangePlayerResult();
      case FIELD_MODEL_CHANGE:
        return new ModelChangeFieldModel();
      default:
        throw new IllegalStateException("Unhandled modelChangeId " + this + ".");
    }
  }

}
