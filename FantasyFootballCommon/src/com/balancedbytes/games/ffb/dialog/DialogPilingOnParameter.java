package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DialogPilingOnParameter implements IDialogParameter {
  
  private String fPlayerId;
  private boolean fReRollInjury;

  public DialogPilingOnParameter() {
    super();
  }
  
  public DialogPilingOnParameter(String pPlayerId, boolean pReRollInjury) {
    fPlayerId = pPlayerId;
    fReRollInjury = pReRollInjury;
  }
  
  public DialogId getId() {
    return DialogId.PILING_ON;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public boolean isReRollInjury() {
    return fReRollInjury;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogPilingOnParameter(getPlayerId(), isReRollInjury());
  }
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    UtilDialogParameter.validateDialogId(this, new DialogIdFactory().forId(pByteArray.getByte()));
    fPlayerId = pByteArray.getString();
    fReRollInjury = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.RE_ROLL_INJURY.addTo(jsonObject, fReRollInjury);
    return jsonObject;
  }
  
  public DialogPilingOnParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fReRollInjury = IJsonOption.RE_ROLL_INJURY.getFrom(jsonObject);
    return this;
  }

}
