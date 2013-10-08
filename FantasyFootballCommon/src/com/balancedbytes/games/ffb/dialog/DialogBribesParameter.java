package com.balancedbytes.games.ffb.dialog;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DialogBribesParameter implements IDialogParameter {
  
  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_ATTRIBUTE_MAX_NR_OF_BRIBES = "maxNrOfBribes";
  private static final String _XML_TAG_PLAYER_LIST = "playerList";
  private static final String _XML_TAG_PLAYER = "player";
  
  private String fTeamId;
  private int fMaxNrOfBribes;
  private List<String> fPlayerIds;

  public DialogBribesParameter() {
    fPlayerIds = new ArrayList<String>();
  }

  public DialogBribesParameter(String pTeamId, int pMaxNrOfBribes) {
    this();
    fTeamId = pTeamId;
    fMaxNrOfBribes = pMaxNrOfBribes;
  }
  
  public DialogId getId() {
    return DialogId.BRIBES;
  }

  public String getTeamId() {
    return fTeamId;
  }
  
  public int getMaxNrOfBribes() {
    return fMaxNrOfBribes;
  }
  
  public void addPlayerId(String pPlayerId) {
    if (StringTool.isProvided(pPlayerId)) {
      fPlayerIds.add(pPlayerId);
    }
  }
  
  public void addPlayerIds(String[] pPlayerIds) {
    if (ArrayTool.isProvided(pPlayerIds)) {
      for (String playerId : pPlayerIds) {
        addPlayerId(playerId);
      }
    }
  }
  
  public String[] getPlayerIds() {
    return fPlayerIds.toArray(new String[fPlayerIds.size()]);
  }
  
  // transformation
  
  public IDialogParameter transform() {
    DialogBribesParameter transformedParameter = new DialogBribesParameter(getTeamId(), getMaxNrOfBribes());
    transformedParameter.addPlayerIds(getPlayerIds());
    return transformedParameter;
  }

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MAX_NR_OF_BRIBES, getMaxNrOfBribes());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    UtilXml.startElement(pHandler, _XML_TAG_PLAYER_LIST);
    for (String playerId : getPlayerIds()) {
      UtilXml.addValueElement(pHandler, _XML_TAG_PLAYER, playerId);
    }
    UtilXml.endElement(pHandler, _XML_TAG_PLAYER_LIST);
    UtilXml.endElement(pHandler, XML_TAG);
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
    pByteList.addByte((byte) getId().getId());
    pByteList.addString(getTeamId());
    pByteList.addByte((byte) getMaxNrOfBribes());
    pByteList.addStringArray(getPlayerIds());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt(); 
    UtilDialogParameter.validateDialogId(this, new DialogIdFactory().forId(pByteArray.getByte()));
    fTeamId = pByteArray.getString();
    fMaxNrOfBribes = pByteArray.getByte();
    addPlayerIds(pByteArray.getStringArray());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    IJsonOption.MAX_NR_OF_BRIBES.addTo(jsonObject, fMaxNrOfBribes);
    IJsonOption.PLAYER_IDS.addTo(jsonObject, getPlayerIds());
    return jsonObject;
  }
  
  public DialogBribesParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    fMaxNrOfBribes = IJsonOption.MAX_NR_OF_BRIBES.getFrom(jsonObject);
    addPlayerIds(IJsonOption.PLAYER_IDS.getFrom(jsonObject));
    return this;
  }

}
