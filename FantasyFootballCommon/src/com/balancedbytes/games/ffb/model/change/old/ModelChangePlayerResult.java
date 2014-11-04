package com.balancedbytes.games.ffb.model.change.old;


import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.model.change.ModelChange;
import com.balancedbytes.games.ffb.model.change.ModelChangeId;

/**
 * 
 * @author Kalimar
 */
public class ModelChangePlayerResult implements IModelChange {
  
  private CommandPlayerResultChange fChange;
  private String fPlayerId;
  private Object fValue;

  protected ModelChangePlayerResult() {
    super();
  }
  
  public ModelChangePlayerResult(CommandPlayerResultChange pChange, String pPlayerId, Object pValue) {
    if (pChange == null) {
      throw new IllegalArgumentException("Parameter change must not be null.");
    }
    fChange = pChange;
    fPlayerId = pPlayerId;
    getChange().getAttributeType().checkValueType(pValue);
    fValue = pValue;
  }
  
  public ModelChangeIdOld getId() {
    return ModelChangeIdOld.PLAYER_RESULT_CHANGE;
  }
  
  public CommandPlayerResultChange getChange() {
    return fChange;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public Object getValue() {
    return fValue;
  }
  
  public void applyTo(Game pGame) {
    Player player = pGame.getPlayerById(getPlayerId());
    PlayerResult playerResult = pGame.getGameResult().getPlayerResult(player);
    switch (getChange()) {
      case SET_SERIOUS_INJURY:
        playerResult.setSeriousInjury((SeriousInjury) getValue());
        break;
      case SET_SEND_TO_BOX_REASON:
        playerResult.setSendToBoxReason((SendToBoxReason) getValue());
        break;
      case SET_SEND_TO_BOX_TURN:
        playerResult.setSendToBoxTurn((Byte) getValue());
        break;
      case SET_SEND_TO_BOX_HALF:
        playerResult.setSendToBoxHalf((Byte) getValue());
        break;
      case SET_SEND_TO_BOX_BY_PLAYER_ID:
        playerResult.setSendToBoxByPlayerId((String) getValue());
        break;
      case SET_COMPLETIONS:
        playerResult.setCompletions((Byte) getValue());
        break;
      case SET_TOUCHDOWNS:
        playerResult.setTouchdowns((Byte) getValue());
        break;
      case SET_INTERCEPTIONS:
        playerResult.setInterceptions((Byte) getValue());
        break;
      case SET_CASUALTIES:
        playerResult.setCasualties((Byte) getValue());
        break;
      case SET_PLAYER_AWARDS:
        playerResult.setPlayerAwards((Byte) getValue());
        break;
      case SET_BLOCKS:
        playerResult.setBlocks((Byte) getValue());
        break;
      case SET_FOULS:
        playerResult.setFouls((Byte) getValue());
        break;
      case SET_TURNS_PLAYED:
        playerResult.setTurnsPlayed((Byte) getValue());
        break;
      case SET_RUSHING:
        playerResult.setRushing((Integer) getValue());
        break;
      case SET_PASSING:
        playerResult.setPassing((Integer) getValue());
        break;
      case SET_CURRENT_SPPS:
        playerResult.setCurrentSpps((Integer) getValue());
        break;
      case SET_DEFECTING:
        playerResult.setDefecting((Boolean) getValue());
        break;
      case SET_SERIOUS_INJURY_DECAY:
        playerResult.setSeriousInjuryDecay((SeriousInjury) getValue());
        break;
      case SET_HAS_USED_SECRET_WEAPON:
        playerResult.setHasUsedSecretWeapon((Boolean) getValue());
        break;
      default:
        throw new IllegalStateException("Unhandled change " + getChange() + ".");
    }
  }
    
  public ModelChange convert() {
    switch (getChange()) {
      case SET_SERIOUS_INJURY:
        return new ModelChange(ModelChangeId.PLAYER_RESULT_SET_SERIOUS_INJURY, getPlayerId(), getValue());
      case SET_SEND_TO_BOX_REASON:
        return new ModelChange(ModelChangeId.PLAYER_RESULT_SET_SEND_TO_BOX_REASON, getPlayerId(), getValue());
      case SET_SEND_TO_BOX_TURN:
        return new ModelChange(ModelChangeId.PLAYER_RESULT_SET_SEND_TO_BOX_TURN, getPlayerId(), getValue());
      case SET_SEND_TO_BOX_HALF:
        return new ModelChange(ModelChangeId.PLAYER_RESULT_SET_SEND_TO_BOX_HALF, getPlayerId(), getValue());
      case SET_SEND_TO_BOX_BY_PLAYER_ID:
        return new ModelChange(ModelChangeId.PLAYER_RESULT_SET_SEND_TO_BOX_BY_PLAYER_ID, getPlayerId(), getValue());
      case SET_COMPLETIONS:
        return new ModelChange(ModelChangeId.PLAYER_RESULT_SET_COMPLETIONS, getPlayerId(), getValue());
      case SET_TOUCHDOWNS:
        return new ModelChange(ModelChangeId.PLAYER_RESULT_SET_TOUCHDOWNS, getPlayerId(), getValue());
      case SET_INTERCEPTIONS:
        return new ModelChange(ModelChangeId.PLAYER_RESULT_SET_INTERCEPTIONS, getPlayerId(), getValue());
      case SET_CASUALTIES:
        return new ModelChange(ModelChangeId.PLAYER_RESULT_SET_CASUALTIES, getPlayerId(), getValue());
      case SET_PLAYER_AWARDS:
        return new ModelChange(ModelChangeId.PLAYER_RESULT_SET_PLAYER_AWARDS, getPlayerId(), getValue());
      case SET_BLOCKS:
        return new ModelChange(ModelChangeId.PLAYER_RESULT_SET_BLOCKS, getPlayerId(), getValue());
      case SET_FOULS:
        return new ModelChange(ModelChangeId.PLAYER_RESULT_SET_FOULS, getPlayerId(), getValue());
      case SET_TURNS_PLAYED:
        return new ModelChange(ModelChangeId.PLAYER_RESULT_SET_TURNS_PLAYED, getPlayerId(), getValue());
      case SET_RUSHING:
        return new ModelChange(ModelChangeId.PLAYER_RESULT_SET_RUSHING, getPlayerId(), getValue());
      case SET_PASSING:
        return new ModelChange(ModelChangeId.PLAYER_RESULT_SET_PASSING, getPlayerId(), getValue());
      case SET_CURRENT_SPPS:
        return new ModelChange(ModelChangeId.PLAYER_RESULT_SET_CURRENT_SPPS, getPlayerId(), getValue());
      case SET_DEFECTING:
        return new ModelChange(ModelChangeId.PLAYER_RESULT_SET_DEFECTING, getPlayerId(), getValue());
      case SET_SERIOUS_INJURY_DECAY:
        return new ModelChange(ModelChangeId.PLAYER_RESULT_SET_SERIOUS_INJURY_DECAY, getPlayerId(), getValue());
      case SET_HAS_USED_SECRET_WEAPON:
        return new ModelChange(ModelChangeId.PLAYER_RESULT_SET_HAS_USED_SECRET_WEAPON, getPlayerId(), getValue());
      default:
        throw new IllegalStateException("Unhandled change " + getChange() + ".");
    }
  }
  
  // transformation
  
  public IModelChange transform() {
    return new ModelChangePlayerResult(getChange(), getPlayerId(), getValue());
  }
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    ModelChangeIdOld changeId = ModelChangeIdOld.fromId(pByteArray.getByte());
    if (getId() != changeId) {
      throw new IllegalStateException("Wrong change id. Expected " + getId().getName() + " received " + ((changeId != null) ? changeId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fChange = CommandPlayerResultChange.fromId(pByteArray.getByte());
    if (getChange() == null) {
      throw new IllegalStateException("Attribute change must not be null.");
    }
    fPlayerId = pByteArray.getString();
    fValue = getChange().getAttributeType().initFrom(pByteArray);
    return byteArraySerializationVersion;
  }
  
}
