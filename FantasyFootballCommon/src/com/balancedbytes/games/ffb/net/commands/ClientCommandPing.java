package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ClientCommandPing extends NetCommand {
  
  private boolean fHasEntropy;
  private byte fEntropy;
  private long fTimestamp;
  
  public ClientCommandPing() {
    super();
  }

  public ClientCommandPing(long pTimestamp, boolean pHasEntropy, byte pEntropy) {
    fTimestamp = pTimestamp;
    fHasEntropy = pHasEntropy;
    fEntropy = pEntropy;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_PING;
  }
  
  public long getTimestamp() {
    return fTimestamp;
  }
  
  public boolean hasEntropy() {
    return fHasEntropy;
  }
  
  public byte getEntropy() {
    return fEntropy;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.TIMESTAMP.addTo(jsonObject, fTimestamp);
    IJsonOption.HAS_ENTROPY.addTo(jsonObject, fHasEntropy);
    IJsonOption.ENTROPY.addTo(jsonObject, fEntropy);
    return jsonObject;
  }
  
  public ClientCommandPing initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fTimestamp = IJsonOption.TIMESTAMP.getFrom(jsonObject);
    fHasEntropy = IJsonOption.HAS_ENTROPY.getFrom(jsonObject);
    fEntropy = (byte) IJsonOption.ENTROPY.getFrom(jsonObject);
    return this;
  }
    
}
