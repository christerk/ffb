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
public class DialogPettyCashParameter implements IDialogParameter {
  
  private String fTeamId;
  private int fTreasury;
  private int fTeamValue;
  private int fOpponentTeamValue;
  
  public DialogPettyCashParameter() {
    super();
  }
  
  public DialogPettyCashParameter(String pTeamId, int pTeamValue, int pTreasury, int pOpponentTeamValue) {
    this();
    fTeamId = pTeamId;
    fTeamValue = pTeamValue;
    fTreasury = pTreasury;
    fOpponentTeamValue = pOpponentTeamValue;
  }
  
  public DialogId getId() {
    return DialogId.PETTY_CASH;
  }
  
  public String getTeamId() {
    return fTeamId;
  }
  
  public int getTeamValue() {
    return fTeamValue;
  }
  
  public int getTreasury() {
    return fTreasury;
  }
  
  public int getOpponentTeamValue() {
    return fOpponentTeamValue;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogPettyCashParameter(getTeamId(), getTeamValue(), getTreasury(), getOpponentTeamValue());
  }
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    UtilDialogParameter.validateDialogId(this, new DialogIdFactory().forId(pByteArray.getByte()));
    fTeamId = pByteArray.getString();
    fTeamValue = pByteArray.getInt();
    fTreasury = pByteArray.getInt();
    fOpponentTeamValue = pByteArray.getInt();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    IJsonOption.TEAM_VALUE.addTo(jsonObject, fTeamValue);
    IJsonOption.TREASURY.addTo(jsonObject, fTreasury);
    IJsonOption.OPPONENT_TEAM_VALUE.addTo(jsonObject, fOpponentTeamValue);
    return jsonObject;
  }
  
  public DialogPettyCashParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    fTeamValue = IJsonOption.TEAM_VALUE.getFrom(jsonObject);
    fTreasury = IJsonOption.TREASURY.getFrom(jsonObject);
    fOpponentTeamValue = IJsonOption.OPPONENT_TEAM_VALUE.getFrom(jsonObject);
    return this;
  }

}
