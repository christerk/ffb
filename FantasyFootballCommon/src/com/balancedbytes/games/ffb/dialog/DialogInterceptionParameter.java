package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DialogInterceptionParameter implements IDialogParameter {
  
  private String fThrowerId;

  public DialogInterceptionParameter() {
    super();
  }
  
  public DialogInterceptionParameter(String pPlayerId) {
    fThrowerId = pPlayerId;
  }
  
  public DialogId getId() {
    return DialogId.INTERCEPTION;
  }
  
  public String getThrowerId() {
    return fThrowerId;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogInterceptionParameter(getThrowerId());
  }
   
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) getId().getId());
    pByteList.addString(getThrowerId());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    UtilDialogParameter.validateDialogId(this, new DialogIdFactory().forId(pByteArray.getByte()));
    fThrowerId = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.THROWER_ID.addTo(jsonObject, fThrowerId);
    return jsonObject;
  }
  
  public DialogInterceptionParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fThrowerId = IJsonOption.THROWER_ID.getFrom(jsonObject);
    return this;
  }

}
