package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.GameList;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandGameList extends ServerCommand {
 
  private GameList fGameList;
  
  public ServerCommandGameList() {
    super();
  }
  
  public ServerCommandGameList(GameList pGameList) {
    this();
    fGameList = pGameList;
  }
  
  public NetCommandId getId() {
    return NetCommandId.SERVER_GAME_LIST;
  }

  public GameList getGameList() {
    return fGameList;
  }
  
  public boolean isReplayable() {
    return false;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    if (getCommandNr() > 0) {
      UtilXml.addAttribute(attributes, XML_ATTRIBUTE_COMMAND_NR, getCommandNr());
    }
    UtilXml.startElement(pHandler, getId().getName(), attributes);
    if (getGameList() != null) {
      getGameList().addToXml(pHandler);
    }
    UtilXml.endElement(pHandler, getId().getName());
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
    pByteList.addSmallInt(getCommandNr());
    boolean hasGameList = (getGameList() != null);
    pByteList.addBoolean(hasGameList);
    if (hasGameList) {
      getGameList().addTo(pByteList);
    }
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setCommandNr(pByteArray.getSmallInt());
    boolean hasGameList = pByteArray.getBoolean();
    if (hasGameList) {
      fGameList = new GameList();
      fGameList.initFrom(pByteArray);
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
    if (fGameList != null) {
      IJsonOption.GAME_LIST.addTo(jsonObject, fGameList.toJsonValue());
    }
    return jsonObject;
  }
  
  public ServerCommandGameList initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    setCommandNr(IJsonOption.COMMAND_NR.getFrom(jsonObject));
    JsonObject gameListObject = IJsonOption.GAME_LIST.getFrom(jsonObject);
    if (gameListObject != null) {
      fGameList = new GameList().initFrom(gameListObject);
    } else {
      fGameList = null;
    }
    return this;
  }
    
}
