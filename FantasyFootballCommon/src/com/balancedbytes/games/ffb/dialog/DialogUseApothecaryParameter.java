package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.SeriousInjuryFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DialogUseApothecaryParameter implements IDialogParameter {
  
  private String fPlayerId;
  private PlayerState fPlayerState;
  private SeriousInjury fSeriousInjury;

  public DialogUseApothecaryParameter() {
    super();
  }
  
  public DialogUseApothecaryParameter(String pPlayerId, PlayerState pPlayerState, SeriousInjury pSeriousInjury) {
    fPlayerId = pPlayerId;
    fPlayerState = pPlayerState;
    fSeriousInjury = pSeriousInjury;
  }
  
  public DialogId getId() {
    return DialogId.USE_APOTHECARY;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public PlayerState getPlayerState() {
    return fPlayerState;
  }
  
  public SeriousInjury getSeriousInjury() {
    return fSeriousInjury;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogUseApothecaryParameter(getPlayerId(), getPlayerState(), getSeriousInjury());
  }
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt(); 
    UtilDialogParameter.validateDialogId(this, new DialogIdFactory().forId(pByteArray.getByte()));
    fPlayerId = pByteArray.getString();
    fPlayerState = new PlayerState(pByteArray.getSmallInt());
    fSeriousInjury = new SeriousInjuryFactory().forId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.PLAYER_STATE.addTo(jsonObject, fPlayerState);
    IJsonOption.SERIOUS_INJURY.addTo(jsonObject, fSeriousInjury);
    return jsonObject;
  }
  
  public DialogUseApothecaryParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fPlayerState = IJsonOption.PLAYER_STATE.getFrom(jsonObject);
    fSeriousInjury = (SeriousInjury) IJsonOption.SERIOUS_INJURY.getFrom(jsonObject);
    return this;
  }

}
