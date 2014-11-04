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
public class DialogWinningsReRollParameter implements IDialogParameter {
  
  private String fTeamId;
  private int fOldRoll;

  public DialogWinningsReRollParameter() {
    super();
  }
  
  public DialogWinningsReRollParameter(String pTeamId, int pOldRoll) {
    fTeamId = pTeamId;
    fOldRoll = pOldRoll;
  }
  
  public DialogId getId() {
    return DialogId.WINNINGS_RE_ROLL;
  }

  public String getTeamId() {
    return fTeamId;
  }
  
  public int getOldRoll() {
    return fOldRoll;
  }

  // transformation
  
  public IDialogParameter transform() {
    return new DialogWinningsReRollParameter(getTeamId(), getOldRoll());
  }
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    UtilDialogParameter.validateDialogId(this, new DialogIdFactory().forId(pByteArray.getByte()));
    fTeamId = pByteArray.getString();
    fOldRoll = pByteArray.getByte();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    IJsonOption.OLD_ROLL.addTo(jsonObject, fOldRoll);
    return jsonObject;
  }
  
  public DialogWinningsReRollParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    fOldRoll = IJsonOption.OLD_ROLL.getFrom(jsonObject);
    return this;
  }

}
