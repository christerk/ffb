package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ClientCommandPing extends NetCommand {
  
  private static final String _XML_ATTRIBUTE_TIMESTAMP = "timestamp";
  private static final String _XML_ATTRIBUTE_HAS_ENTROPY = "hasEntropy";
  private static final String _XML_ATTRIBUTE_ENTROPY = "entropy";
  
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
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TIMESTAMP, getTimestamp());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_HAS_ENTROPY, hasEntropy());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ENTROPY, getEntropy());
    UtilXml.addEmptyElement(pHandler, getId().getName(), attributes);
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addLong(getTimestamp());
    pByteList.addBoolean(hasEntropy());
    pByteList.addByte(getEntropy());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTimestamp = pByteArray.getLong();
    fHasEntropy = pByteArray.getBoolean();
    fEntropy = pByteArray.getByte();
    return byteArraySerializationVersion;
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
