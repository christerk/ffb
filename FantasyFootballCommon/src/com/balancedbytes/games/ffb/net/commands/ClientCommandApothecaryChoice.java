package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.SeriousInjuryFactory;
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
public class ClientCommandApothecaryChoice extends NetCommand {
   
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_PLAYER_STATE = "playerState";
  private static final String _XML_ATTRIBUTE_SERIOUS_INJURY = "seriousInjury";
  
  private String fPlayerId;
  private PlayerState fPlayerState;
  private SeriousInjury fSeriousInjury;
  
  public ClientCommandApothecaryChoice() {
    super();
  }

  public ClientCommandApothecaryChoice(String pPlayerId, PlayerState pPlayerState, SeriousInjury pSeriousInjury) {
    fPlayerId = pPlayerId;
    fPlayerState = pPlayerState;
    fSeriousInjury = pSeriousInjury;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_APOTHECARY_CHOICE;
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
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_STATE, (getPlayerState() != null) ? getPlayerState().getId() : 0);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SERIOUS_INJURY, (getSeriousInjury() != null) ? getSeriousInjury().getName() : null);
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
    pByteList.addString(getPlayerId());
    pByteList.addSmallInt((getPlayerState() != null) ? getPlayerState().getId() : 0);
    pByteList.addByte((byte) ((getSeriousInjury() != null) ? getSeriousInjury().getId() : 0));
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fPlayerState = new PlayerState(pByteArray.getSmallInt());
    fSeriousInjury = new SeriousInjuryFactory().forId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }

  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.PLAYER_STATE.addTo(jsonObject, fPlayerState);
    IJsonOption.SERIOUS_INJURY.addTo(jsonObject, fSeriousInjury);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fPlayerState = IJsonOption.PLAYER_STATE.getFrom(jsonObject);
    fSeriousInjury = (SeriousInjury) IJsonOption.SERIOUS_INJURY.getFrom(jsonObject);
  }
  
}
