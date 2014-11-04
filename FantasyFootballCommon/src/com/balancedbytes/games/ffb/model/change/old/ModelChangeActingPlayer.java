package com.balancedbytes.games.ffb.model.change.old;


import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.change.ModelChange;
import com.balancedbytes.games.ffb.model.change.ModelChangeId;

/**
 * 
 * @author Kalimar
 */
public class ModelChangeActingPlayer implements IModelChange {
  
  private CommandActingPlayerChange fChange;
  private Object fValue;
  
  protected ModelChangeActingPlayer() {
    super();
  }
  
  public ModelChangeActingPlayer(CommandActingPlayerChange pChange, Object pValue) {
    if (pChange == null) {
      throw new IllegalArgumentException("Parameter change must not be null.");
    }
    fChange = pChange;
    getChange().getAttributeType().checkValueType(pValue);
    fValue = pValue;
  }
  
  public ModelChangeIdOld getId() {
    return ModelChangeIdOld.ACTING_PLAYER_CHANGE;
  }
  
  public CommandActingPlayerChange getChange() {
    return fChange;
  }
  
  public Object getValue() {
    return fValue;
  }
  
  public void applyTo(Game pGame) {
    ActingPlayer actingPlayer = pGame.getActingPlayer();
    switch (getChange()) {
      case SET_PLAYER_ID:
        actingPlayer.setPlayerId((String) getValue());
        break;
      case SET_STRENGTH:
        actingPlayer.setStrength((Byte) getValue());
        break;
      case SET_CURRENT_MOVE:
        actingPlayer.setCurrentMove((Byte) getValue());
        break;
      case SET_GOING_FOR_IT:
        actingPlayer.setGoingForIt((Boolean) getValue());
        break;
      case SET_DODGING:
        actingPlayer.setDodging((Boolean) getValue());
        break;
      case SET_LEAPING:
        actingPlayer.setLeaping((Boolean) getValue());
        break;
      case SET_STANDING_UP:
        actingPlayer.setStandingUp((Boolean) getValue());
        break;
      case SET_SUFFERING_BLOOD_LUST:
        actingPlayer.setSufferingBloodLust((Boolean) getValue());
        break;
      case SET_SUFFERING_ANIMOSITY:
        actingPlayer.setSufferingAnimosity((Boolean) getValue());
        break;
      case SET_HAS_BLOCKED:
        actingPlayer.setHasBlocked((Boolean) getValue());
        break;
      case SET_HAS_FOULED:
        actingPlayer.setHasFouled((Boolean) getValue());
        break;
      case SET_HAS_PASSED:
        actingPlayer.setHasPassed((Boolean) getValue());
        break;
      case SET_HAS_MOVED:
        actingPlayer.setHasMoved((Boolean) getValue());
        break;
      case SET_HAS_FED:
        actingPlayer.setHasFed((Boolean) getValue());
        break;
      case SET_PLAYER_ACTION:
        actingPlayer.setPlayerAction((PlayerAction) getValue());
        break;
      case MARK_SKILL_USED:
        actingPlayer.markSkillUsed((Skill) getValue());
        break;
      default:
        throw new IllegalStateException("Unhandled change " + getChange() + ".");
    }
  }
  
  public ModelChange convert() {
    switch (getChange()) {
      case SET_PLAYER_ID:
        return new ModelChange(ModelChangeId.ACTING_PLAYER_SET_PLAYER_ID, null, getValue());
      case SET_STRENGTH:
        return new ModelChange(ModelChangeId.ACTING_PLAYER_SET_STRENGTH, null, getValue());
      case SET_CURRENT_MOVE:
        return new ModelChange(ModelChangeId.ACTING_PLAYER_SET_CURRENT_MOVE, null, getValue());
      case SET_GOING_FOR_IT:
        return new ModelChange(ModelChangeId.ACTING_PLAYER_SET_GOING_FOR_IT, null, getValue());
      case SET_DODGING:
        return new ModelChange(ModelChangeId.ACTING_PLAYER_SET_DODGING, null, getValue());
      case SET_LEAPING:
        return new ModelChange(ModelChangeId.ACTING_PLAYER_SET_LEAPING, null, getValue());
      case SET_STANDING_UP:
        return new ModelChange(ModelChangeId.ACTING_PLAYER_SET_STANDING_UP, null, getValue());
      case SET_SUFFERING_BLOOD_LUST:
        return new ModelChange(ModelChangeId.ACTING_PLAYER_SET_SUFFERING_BLOOD_LUST, null, getValue());
      case SET_SUFFERING_ANIMOSITY:
        return new ModelChange(ModelChangeId.ACTING_PLAYER_SET_SUFFERING_ANIMOSITY, null, getValue());
      case SET_HAS_BLOCKED:
        return new ModelChange(ModelChangeId.ACTING_PLAYER_SET_HAS_BLOCKED, null, getValue());
      case SET_HAS_FOULED:
        return new ModelChange(ModelChangeId.ACTING_PLAYER_SET_HAS_FOULED, null, getValue());
      case SET_HAS_PASSED:
        return new ModelChange(ModelChangeId.ACTING_PLAYER_SET_HAS_PASSED, null, getValue());
      case SET_HAS_MOVED:
        return new ModelChange(ModelChangeId.ACTING_PLAYER_SET_HAS_MOVED, null, getValue());
      case SET_HAS_FED:
        return new ModelChange(ModelChangeId.ACTING_PLAYER_SET_HAS_FED, null, getValue());
      case SET_PLAYER_ACTION:
        return new ModelChange(ModelChangeId.ACTING_PLAYER_SET_PLAYER_ACTION, null, getValue());
      case MARK_SKILL_USED:
        return new ModelChange(ModelChangeId.ACTING_PLAYER_MARK_SKILL_USED, null, getValue());
      default:
        throw new IllegalStateException("Unhandled change " + getChange() + ".");
    }
  }
 
  // transformation
  
  public IModelChange transform() {
    return new ModelChangeActingPlayer(getChange(), getValue());
  }
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    ModelChangeIdOld changeId = ModelChangeIdOld.fromId(pByteArray.getByte());
    if (getId() != changeId) {
      throw new IllegalStateException("Wrong change id. Expected " + getId().getName() + " received " + ((changeId != null) ? changeId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fChange = CommandActingPlayerChange.fromId(pByteArray.getByte());
    if (getChange() == null) {
      throw new IllegalStateException("Attribute change must not be null.");
    }
    fValue = getChange().getAttributeType().initFrom(pByteArray);
    return byteArraySerializationVersion;
  }
  
}
