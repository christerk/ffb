package com.balancedbytes.games.ffb.model.change.old;


import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.Inducement;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.TurnData;
import com.balancedbytes.games.ffb.model.change.ModelChange;
import com.balancedbytes.games.ffb.model.change.ModelChangeId;

/**
 * 
 * @author Kalimar
 */
public class ModelChangeTurnData implements IModelChange {
  
  private CommandTurnDataChange fChange;
  private boolean fHomeData;
  private Object fValue;

  protected ModelChangeTurnData() {
    super();
  }
  
  public ModelChangeTurnData(CommandTurnDataChange pChange, boolean pHomeData, Object pValue) {
    if (pChange == null) {
      throw new IllegalArgumentException("Parameter change must not be null.");
    }
    fChange = pChange;
    fHomeData = pHomeData;
    getChange().getAttributeType().checkValueType(pValue);
    fValue = pValue;
  }
  
  public ModelChangeIdOld getId() {
    return ModelChangeIdOld.TURN_DATA_CHANGE;
  }
  
  public CommandTurnDataChange getChange() {
    return fChange;
  }
  
  public boolean isHomeData() {
    return fHomeData;
  }
  
  public Object getValue() {
    return fValue;
  }
  
  public void applyTo(Game pGame) {
    TurnData turnData = isHomeData() ? pGame.getTurnDataHome() : pGame.getTurnDataAway();
    switch (getChange()) {
      case SET_TURN_NR:
        turnData.setTurnNr((Byte) getValue());
        break;
      case SET_RE_ROLLS:
        turnData.setReRolls((Byte) getValue());
        break;
      case SET_APOTHECARIES:
        turnData.setApothecaries((Byte) getValue());
        break;
      case SET_BLITZ_USED:
        turnData.setBlitzUsed((Boolean) getValue());
        break;
      case SET_FOUL_USED:
        turnData.setFoulUsed((Boolean) getValue());
        break;
      case SET_RE_ROLL_USED:
        turnData.setReRollUsed((Boolean) getValue());
        break;
      case SET_HAND_OVER_USED:
        turnData.setHandOverUsed((Boolean) getValue());
        break;
      case SET_PASS_USED:
        turnData.setPassUsed((Boolean) getValue());
        break;
      case ADD_INDUCEMENT:
        turnData.getInducementSet().addInducement((Inducement) getValue());
        break;
      case REMOVE_INDUCEMENT:
        turnData.getInducementSet().removeInducement((Inducement) getValue());
        break;
      case SET_FIRST_TURN_AFTER_KICKOFF:
        turnData.setFirstTurnAfterKickoff((Boolean) getValue());
        break;
      case SET_TURN_STARTED:
      	turnData.setTurnStarted((Boolean) getValue());
      	break;
      case ACTIVATE_CARD:
      	turnData.getInducementSet().activateCard((Card) getValue());
      	break;
      case DEACTIVATE_CARD:
      	turnData.getInducementSet().deactivateCard((Card) getValue());
      	break;
      case ADD_AVAILABLE_CARD:
      	turnData.getInducementSet().addAvailableCard((Card) getValue());
      	break;
      case REMOVE_AVAILABLE_CARD:
      	turnData.getInducementSet().removeAvailableCard((Card) getValue());
      	break;
      default:
        throw new IllegalStateException("Unhandled change " + getChange() + ".");
    }
  }
  
//case TURN_DATA_SET_LEADER_STATE:
//  getTurnData(pGame, isHomeData(pModelChange)).setLeaderState((LeaderState) pModelChange.getValue());
//  return true;
  
  public ModelChange convert() {
    switch (getChange()) {
      case SET_TURN_NR:
        return new ModelChange(ModelChangeId.TURN_DATA_SET_TURN_NR, getHomeDataKey(), getValue());
      case SET_RE_ROLLS:
        return new ModelChange(ModelChangeId.TURN_DATA_SET_RE_ROLLS, getHomeDataKey(), getValue());
      case SET_APOTHECARIES:
        return new ModelChange(ModelChangeId.TURN_DATA_SET_APOTHECARIES, getHomeDataKey(), getValue());
      case SET_BLITZ_USED:
        return new ModelChange(ModelChangeId.TURN_DATA_SET_BLITZ_USED, getHomeDataKey(), getValue());
      case SET_FOUL_USED:
        return new ModelChange(ModelChangeId.TURN_DATA_SET_FOUL_USED, getHomeDataKey(), getValue());
      case SET_RE_ROLL_USED:
        return new ModelChange(ModelChangeId.TURN_DATA_SET_RE_ROLL_USED, getHomeDataKey(), getValue());
      case SET_HAND_OVER_USED:
        return new ModelChange(ModelChangeId.TURN_DATA_SET_HAND_OVER_USED, getHomeDataKey(), getValue());
      case SET_PASS_USED:
        return new ModelChange(ModelChangeId.TURN_DATA_SET_PASS_USED, getHomeDataKey(), getValue());
      case SET_FIRST_TURN_AFTER_KICKOFF:
        return new ModelChange(ModelChangeId.TURN_DATA_SET_FIRST_TURN_AFTER_KICKOFF, getHomeDataKey(), getValue());
      case SET_TURN_STARTED:
        return new ModelChange(ModelChangeId.TURN_DATA_SET_TURN_STARTED, getHomeDataKey(), getValue());
      case ADD_INDUCEMENT:
        return new ModelChange(ModelChangeId.INDUCEMENT_SET_ADD_INDUCEMENT, getHomeDataKey(), getValue());
      case REMOVE_INDUCEMENT:
        return new ModelChange(ModelChangeId.INDUCEMENT_SET_REMOVE_INDUCEMENT, getHomeDataKey(), getValue());
      case ACTIVATE_CARD:
        return new ModelChange(ModelChangeId.INDUCEMENT_SET_ACTIVATE_CARD, getHomeDataKey(), getValue());
      case DEACTIVATE_CARD:
        return new ModelChange(ModelChangeId.INDUCEMENT_SET_DEACTIVATE_CARD, getHomeDataKey(), getValue());
      case ADD_AVAILABLE_CARD:
        return new ModelChange(ModelChangeId.INDUCEMENT_SET_ADD_AVAILABLE_CARD, getHomeDataKey(), getValue());
      case REMOVE_AVAILABLE_CARD:
        return new ModelChange(ModelChangeId.INDUCEMENT_SET_REMOVE_AVAILABLE_CARD, getHomeDataKey(), getValue());
      default:
        throw new IllegalStateException("Unhandled change " + getChange() + ".");
    }
  }
  
  private String getHomeDataKey() {
    return isHomeData() ? ModelChange.HOME : ModelChange.AWAY;
  }
 
  // transformation
  
  public IModelChange transform() {
    return new ModelChangeTurnData(getChange(), !isHomeData(), getValue());
  }
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    ModelChangeIdOld changeId = ModelChangeIdOld.fromId(pByteArray.getByte());
    if (getId() != changeId) {
      throw new IllegalStateException("Wrong change id. Expected " + getId().getName() + " received " + ((changeId != null) ? changeId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fChange = CommandTurnDataChange.fromId(pByteArray.getByte());
    fHomeData = pByteArray.getBoolean();
    if (getChange() == null) {
      throw new IllegalStateException("Attribute change must not be null.");
    }
    fValue = getChange().getAttributeType().initFrom(pByteArray);
    return byteArraySerializationVersion;
  }
  
}
