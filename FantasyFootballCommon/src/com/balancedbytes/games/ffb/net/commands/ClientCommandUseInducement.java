package com.balancedbytes.games.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardFactory;
import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.InducementTypeFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ClientCommandUseInducement extends NetCommand {
  
  private static final String _XML_ATTRIBUTE_INDUCEMENT = "inducement";
  private static final String _XML_ATTRIBUTE_CARD = "card";

  private static final String _XML_TAG_PLAYER = "player";
  private static final String _XML_ATTRIBUTE_ID = "id";
  
  private InducementType fInducementType;
  private Card fCard;
  private List<String> fPlayerIds;
  
  public ClientCommandUseInducement() {
    fPlayerIds = new ArrayList<String>();
  }

  public ClientCommandUseInducement(InducementType pInducementType) {
    this();
    fInducementType = pInducementType;
  }
  
  public ClientCommandUseInducement(InducementType pInducement, String pPlayerId) {
    this(pInducement);
    addPlayerId(pPlayerId);
  }
  
  public ClientCommandUseInducement(Card pCard) {
  	this();
  	fCard = pCard;
  }
  
  public ClientCommandUseInducement(Card pCard, String pPlayerId) {
  	this(pCard);
  	addPlayerId(pPlayerId);
  }

  public ClientCommandUseInducement(InducementType pInducement, String[] pPlayerIds) {
    this(pInducement);
    addPlayerIds(pPlayerIds);
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_USE_INDUCEMENT;
  }
  
  public InducementType getInducementType() {
    return fInducementType;
  }
  
  public Card getCard() {
	  return fCard;
  }
  
  public String[] getPlayerIds() {
    return fPlayerIds.toArray(new String[fPlayerIds.size()]);
  }
  
  public boolean hasPlayerId(String pPlayerId) {
    return fPlayerIds.contains(pPlayerId);
  }

  private void addPlayerId(String pPlayerId) {
    if (StringTool.isProvided(pPlayerId)) {
      fPlayerIds.add(pPlayerId);
    }
  }
  
  private void addPlayerIds(String[] pPlayerIds) {
    if (ArrayTool.isProvided(pPlayerIds)) {
      for (String playerId : pPlayerIds) {
        addPlayerId(playerId);
      }
    }
  }

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    if (getCard() != null) {
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CARD, getCard().getName());
    } else {
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_INDUCEMENT, (getInducementType() != null) ? getInducementType().getName() : null);
    }
    UtilXml.startElement(pHandler, getId().getName(), attributes);
    String[] playerIds = getPlayerIds();
    if (ArrayTool.isProvided(playerIds)) {
      for (String playerId : playerIds) {
        attributes = new AttributesImpl();
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ID, playerId);
        UtilXml.addEmptyElement(pHandler, _XML_TAG_PLAYER, attributes);
      }
    }
    UtilXml.endElement(pHandler, getId().getName());
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
    pByteList.addByte((byte) ((getInducementType() != null) ? getInducementType().getId() : 0));
    pByteList.addStringArray(getPlayerIds());
    pByteList.addSmallInt((getCard() != null) ? getCard().getId() : 0);
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fInducementType = new InducementTypeFactory().forId(pByteArray.getByte());
    addPlayerIds(pByteArray.getStringArray());
    if (byteArraySerializationVersion > 1) {
    	fCard = new CardFactory().forId(pByteArray.getSmallInt());
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.INDUCEMENT_TYPE.addTo(jsonObject, fInducementType);
    IJsonOption.PLAYER_IDS.addTo(jsonObject, fPlayerIds);
    IJsonOption.CARD.addTo(jsonObject, fCard);
    return jsonObject;
  }

  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fInducementType = (InducementType) IJsonOption.INDUCEMENT_TYPE.getFrom(jsonObject);
    addPlayerIds(IJsonOption.PLAYER_IDS.getFrom(jsonObject));
    fCard = (Card) IJsonOption.CARD.getFrom(jsonObject);
  }
      
}
