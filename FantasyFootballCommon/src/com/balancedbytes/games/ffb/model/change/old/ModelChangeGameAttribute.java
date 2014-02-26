package com.balancedbytes.games.ffb.model.change.old;


import java.util.Date;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.GameOptionValue;
import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.change.ModelChange;
import com.balancedbytes.games.ffb.model.change.ModelChangeId;

/**
 * 
 * @author Kalimar
 */
public class ModelChangeGameAttribute implements IModelChange {
  
  private CommandGameAttributeChange fChange;
  private Object fValue;
  
  protected ModelChangeGameAttribute() {
    super();
  }
  
  public ModelChangeGameAttribute(CommandGameAttributeChange pChange, Object pValue) {
    if (pChange == null) {
      throw new IllegalArgumentException("Parameter change must not be null.");
    }
    fChange = pChange;
    getChange().getAttributeType().checkValueType(pValue);
    fValue = pValue;
  }
  
  public ModelChangeIdOld getId() {
    return ModelChangeIdOld.GAME_ATTRIBUTE_CHANGE;
  }
  
  public CommandGameAttributeChange getChange() {
    return fChange;
  }
  
  public Object getValue() {
    return fValue;
  }
  
  public void applyTo(Game pGame) {
    switch (getChange()) {
      case SET_ID:
        pGame.setId((Long) getValue());
        break;
      case SET_STARTED:
        pGame.setStarted((Date) getValue());
        break;
      case SET_FINISHED:
        pGame.setFinished((Date) getValue());
        break;
      case SET_HALF:
        pGame.setHalf((Byte) getValue());
        break;
      case SET_TURN_MODE:
        pGame.setTurnMode((TurnMode) getValue());
        break;
      case SET_PASS_COORDINATE:
        pGame.setPassCoordinate((FieldCoordinate) getValue());
        break;
      case SET_HOME_PLAYING:
        pGame.setHomePlaying((Boolean) getValue());
        break;
      case SET_HOME_FIRST_OFFENSE:
        pGame.setHomeFirstOffense((Boolean) getValue());
        break;
      case SET_SETUP_OFFENSE:
        pGame.setSetupOffense((Boolean) getValue());
        break;
      case SET_WAITING_FOR_OPPONENT:
        pGame.setWaitingForOpponent((Boolean) getValue());
        break;
      case SET_DIALOG_PARAMETER:
        pGame.setDialogParameter((IDialogParameter) getValue());
        break;
      case SET_DEFENDER_ID:
        pGame.setDefenderId((String) getValue());
        break;
      case SET_DEFENDER_ACTION:
        pGame.setDefenderAction((PlayerAction) getValue());
        break;
      case SET_TIMEOUT_POSSIBLE:
        pGame.setTimeoutPossible((Boolean) getValue());
        break;
      case SET_TIMEOUT_ENFORCED:
        pGame.setTimeoutEnforced((Boolean) getValue());
        break;
      case SET_CONCESSION_POSSIBLE:
        pGame.setConcessionPossible((Boolean) getValue());
        break;
      case SET_TESTING:
        pGame.setTesting((Boolean) getValue());
        break;
      case SET_SCHEDULED:
        pGame.setScheduled((Date) getValue());
        break;
      case SET_THROWER_ID:
      	pGame.setThrowerId((String) getValue());
      	break;
      case SET_THROWER_ACTION:
      	pGame.setThrowerAction((PlayerAction) getValue());
      	break;
      case ADD_OPTION:
      	pGame.getOptions().addOption((GameOptionValue) getValue());
      	break;
      default:
        throw new IllegalStateException("Unhandled change " + getChange() + ".");
    }
  }
  
//case GAME_SET_TURN_TIME:
//  pGame.setTurnTime((Long) pModelChange.getValue());
//  return true;
  
  public ModelChange convert() {
    switch (getChange()) {
      case SET_ID:
        return new ModelChange(ModelChangeId.GAME_SET_ID, null, getValue());
      case SET_STARTED:
        return new ModelChange(ModelChangeId.GAME_SET_STARTED, null, getValue());
      case SET_FINISHED:
        return new ModelChange(ModelChangeId.GAME_SET_FINISHED, null, getValue());
      case SET_HALF:
        return new ModelChange(ModelChangeId.GAME_SET_HALF, null, getValue());
      case SET_TURN_MODE:
        return new ModelChange(ModelChangeId.GAME_SET_TURN_MODE, null, getValue());
      case SET_PASS_COORDINATE:
        return new ModelChange(ModelChangeId.GAME_SET_PASS_COORDINATE, null, getValue());
      case SET_HOME_PLAYING:
        return new ModelChange(ModelChangeId.GAME_SET_HOME_PLAYING, null, getValue());
      case SET_HOME_FIRST_OFFENSE:
        return new ModelChange(ModelChangeId.GAME_SET_HOME_FIRST_OFFENSE, null, getValue());
      case SET_SETUP_OFFENSE:
        return new ModelChange(ModelChangeId.GAME_SET_SETUP_OFFENSE, null, getValue());
      case SET_WAITING_FOR_OPPONENT:
        return new ModelChange(ModelChangeId.GAME_SET_WAITING_FOR_OPPONENT, null, getValue());
      case SET_DIALOG_PARAMETER:
        return new ModelChange(ModelChangeId.GAME_SET_DIALOG_PARAMETER, null, getValue());
      case SET_DEFENDER_ID:
        return new ModelChange(ModelChangeId.GAME_SET_DEFENDER_ID,(String) getValue(), null);
      case SET_DEFENDER_ACTION:
        return new ModelChange(ModelChangeId.GAME_SET_DEFENDER_ACTION, null, getValue());
      case SET_TIMEOUT_POSSIBLE:
        return new ModelChange(ModelChangeId.GAME_SET_TIMEOUT_POSSIBLE, null, getValue());
      case SET_TIMEOUT_ENFORCED:
        return new ModelChange(ModelChangeId.GAME_SET_TIMEOUT_ENFORCED, null, getValue());
      case SET_CONCESSION_POSSIBLE:
        return new ModelChange(ModelChangeId.GAME_SET_CONCESSION_POSSIBLE, null, getValue());
      case SET_TESTING:
        return new ModelChange(ModelChangeId.GAME_SET_TESTING, null, getValue());
      case SET_SCHEDULED:
        return new ModelChange(ModelChangeId.GAME_SET_SCHEDULED, null, getValue());
      case SET_THROWER_ID:
        return new ModelChange(ModelChangeId.GAME_SET_THROWER_ID, (String) getValue(), null);
      case SET_THROWER_ACTION:
        return new ModelChange(ModelChangeId.GAME_SET_THROWER_ACTION, null, getValue());
      case ADD_OPTION:
        return new ModelChange(ModelChangeId.GAME_OPTIONS_ADD_OPTION, null, getValue());
      default:
        throw new IllegalStateException("Unhandled change " + getChange() + ".");
    }
  }

  // transformation
  
  public IModelChange transform() {
    switch (getChange()) {
      case SET_PASS_COORDINATE:
        return new ModelChangeGameAttribute(getChange(), FieldCoordinate.transform((FieldCoordinate) getValue()));
      case SET_HOME_PLAYING:
      case SET_HOME_FIRST_OFFENSE:
        return new ModelChangeGameAttribute(getChange(), !((Boolean) getValue()));
      case SET_DIALOG_PARAMETER:
        if (getValue() != null) {
          return new ModelChangeGameAttribute(getChange(), ((IDialogParameter) getValue()).transform());
        } else {
          return new ModelChangeGameAttribute(getChange(), getValue());
        }
      default:
        return new ModelChangeGameAttribute(getChange(), getValue());
    }
  }
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    ModelChangeIdOld changeId = ModelChangeIdOld.fromId(pByteArray.getByte());
    if (getId() != changeId) {
      throw new IllegalStateException("Wrong change id. Expected " + getId().getName() + " received " + ((changeId != null) ? changeId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fChange = CommandGameAttributeChange.fromId(pByteArray.getByte());
    if (getChange() == null) {
      throw new IllegalStateException("Attribute change must not be null.");
    }
    fValue = getChange().getAttributeType().initFrom(pByteArray);
    return byteArraySerializationVersion;
  }
  
}
