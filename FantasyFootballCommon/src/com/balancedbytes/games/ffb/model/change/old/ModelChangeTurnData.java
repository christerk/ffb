package com.balancedbytes.games.ffb.model.change.old;


import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.Inducement;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.TurnData;

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
 
  // transformation
  
  public IModelChange transform() {
    return new ModelChangeTurnData(getChange(), !isHomeData(), getValue());
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addByte((byte) getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) getChange().getId());
    pByteList.addBoolean(isHomeData());
    getChange().getAttributeType().addTo(pByteList, getValue());
  }
  
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
