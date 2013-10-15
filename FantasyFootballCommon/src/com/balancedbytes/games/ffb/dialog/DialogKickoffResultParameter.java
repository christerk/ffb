package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.KickoffResult;
import com.balancedbytes.games.ffb.KickoffResultFactory;
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
public class DialogKickoffResultParameter implements IDialogParameter {
  
  private KickoffResult fKickoffResult;

  public DialogKickoffResultParameter() {
    super();
  }
  
  public DialogKickoffResultParameter(KickoffResult pKickoffResult) {
    fKickoffResult = pKickoffResult;
  }
  
  public DialogId getId() {
    return DialogId.KICKOFF_RESULT;
  }
  
  public KickoffResult getKickoffResult() {
    return fKickoffResult;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogKickoffResultParameter(getKickoffResult());
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) getId().getId());
    pByteList.addByte((byte) ((getKickoffResult() != null) ? getKickoffResult().getId() : 0));
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    UtilDialogParameter.validateDialogId(this, new DialogIdFactory().forId(pByteArray.getByte()));
    fKickoffResult = new KickoffResultFactory().forId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.KICKOFF_RESULT.addTo(jsonObject, fKickoffResult);
    return jsonObject;
  }
  
  public DialogKickoffResultParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fKickoffResult = (KickoffResult) IJsonOption.KICKOFF_RESULT.getFrom(jsonObject);
    return this;
  }

}
