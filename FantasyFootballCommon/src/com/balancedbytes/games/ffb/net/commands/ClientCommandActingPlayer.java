package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerActionFactory;
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
public class ClientCommandActingPlayer extends NetCommand {

  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_PLAYER_ACTION = "playerAction";
  private static final String _XML_ATTRIBUTE_LEAPING = "leaping";

  private String fPlayerId;
  private PlayerAction fPlayerAction;
  private boolean fLeaping;

  public ClientCommandActingPlayer() {
    super();
  }

  public ClientCommandActingPlayer(String pPlayerId, PlayerAction pPlayerAction, boolean pLeaping) {
    fPlayerId = pPlayerId;
    fPlayerAction = pPlayerAction;
    fLeaping = pLeaping;
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_ACTING_PLAYER;
  }

  public String getPlayerId() {
    return fPlayerId;
  }

  public PlayerAction getPlayerAction() {
    return fPlayerAction;
  }

  public boolean isLeaping() {
    return fLeaping;
  }

  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ACTION, (getPlayerAction() != null) ? getPlayerAction().getName() : null);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_LEAPING, isLeaping());
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
    if (getPlayerAction() != null) {
      pByteList.addByte((byte) getPlayerAction().getId());
    } else {
      pByteList.addByte((byte) 0);
    }
    pByteList.addBoolean(isLeaping());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fPlayerAction = new PlayerActionFactory().forId(pByteArray.getByte());
    fLeaping = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }

  // JSON serialization

  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.PLAYER_ACTION.addTo(jsonObject, fPlayerAction);
    IJsonOption.LEAPING.addTo(jsonObject, fLeaping);
    return jsonObject;
  }

  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fPlayerAction = (PlayerAction) IJsonOption.PLAYER_ACTION.getFrom(jsonObject);
    fLeaping = IJsonOption.LEAPING.getFrom(jsonObject);
  }

}
