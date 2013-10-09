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
public class ClientCommandHandOver extends NetCommand implements ICommandWithActingPlayer {
  
	private static final String _XML_ATTRIBUTE_ACTING_PLAYER_ID = "actingPlayerId";
  private static final String _XML_ATTRIBUTE_CATCHER_ID = "catcherId";

  private String fActingPlayerId;
  private String fCatcherId;
  
  public ClientCommandHandOver() {
    super();
  }

  public ClientCommandHandOver(String pActingPlayerId, String pCatcherId) {
  	fActingPlayerId = pActingPlayerId;
    fCatcherId = pCatcherId;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_HAND_OVER;
  }
  
  public String getActingPlayerId() {
    return fActingPlayerId;
  }
  
  public String getCatcherId() {
    return fCatcherId;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ACTING_PLAYER_ID, getActingPlayerId());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CATCHER_ID, getCatcherId());
    UtilXml.addEmptyElement(pHandler, getId().getName(), attributes);
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 2;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getActingPlayerId());
    pByteList.addString(getCatcherId());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    if (byteArraySerializationVersion > 1) {
    	fActingPlayerId = pByteArray.getString();
    }
    fCatcherId = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
    IJsonOption.CATCHER_ID.addTo(jsonObject, fCatcherId);
    return jsonObject;
  }
  
  public ClientCommandHandOver initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(jsonObject);
    fCatcherId = IJsonOption.CATCHER_ID.getFrom(jsonObject);
    return this;
  }
    
}
