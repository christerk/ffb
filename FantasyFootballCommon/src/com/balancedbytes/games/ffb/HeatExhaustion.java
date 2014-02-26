package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArrayReadable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class HeatExhaustion implements IByteArrayReadable, IJsonSerializable {
  
  private String fPlayerId;
  private boolean fExhausted;
  private int fRoll;
  
  public HeatExhaustion() {
    super();
  }

  public HeatExhaustion(String pPlayerId, boolean pExhausted, int pRoll) {
    fPlayerId = pPlayerId;
    fExhausted = pExhausted;
    fRoll = pRoll;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }

  public boolean isExhausted() {
    return fExhausted;
  }

  public int getRoll() {
    return fRoll;
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getPlayerId());
    pByteList.addBoolean(isExhausted());
    pByteList.addByte((byte) getRoll());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fExhausted = pByteArray.getBoolean();
    fRoll = pByteArray.getByte();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.EXHAUSTED.addTo(jsonObject, fExhausted);
    IJsonOption.ROLL.addTo(jsonObject, fRoll);
    return jsonObject;
  }
  
  public HeatExhaustion initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fExhausted = IJsonOption.EXHAUSTED.getFrom(jsonObject);
    fRoll = IJsonOption.ROLL.getFrom(jsonObject);
    return this;
  }

  
}
