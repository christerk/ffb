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
public class DialogJourneymenParameter implements IDialogParameter {
  
  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_ATTRIBUTE_SLOTS = "slots";
  
  private static final String _XML_TAG_POSITION_IDS = "positionIds";
  private static final String _XML_TAG_POSITION_ID = "positionId";
  
  private String fTeamId;
  private int fSlots;
  private List<String> fPositionIds;

  public DialogJourneymenParameter() {
    fPositionIds = new ArrayList<String>();
  }
  
  public DialogJourneymenParameter(String pTeamId, int pSlots, String[] pPositionIds) {
    this();
    fTeamId = pTeamId;
    fSlots = pSlots;
    addPositionIds(pPositionIds);
  }
  
  public DialogId getId() {
    return DialogId.JOURNEYMEN;
  }
  
  public String getTeamId() {
    return fTeamId;
  }
  
  public int getSlots() {
    return fSlots;
  }
  
  private void addPositionId(String pPositionId) {
    if (StringTool.isProvided(pPositionId)) {
      fPositionIds.add(pPositionId);
    }
  }

  private void addPositionIds(String[] pPositionIds) {
    if (ArrayTool.isProvided(pPositionIds)) {
      for (String positionId : pPositionIds) {
        addPositionId(positionId);
      }
    }
  }
  
  public String[] getPositionIds() {
    return fPositionIds.toArray(new String[fPositionIds.size()]);
  }

  // transformation
  
  public IDialogParameter transform() {
    return new DialogJourneymenParameter(getTeamId(), getSlots(), getPositionIds());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SLOTS, getSlots());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    String[] positionIds = getPositionIds();
    if (ArrayTool.isProvided(positionIds)) {
      UtilXml.startElement(pHandler, _XML_TAG_POSITION_IDS);
      for (String positionId : positionIds) {
        UtilXml.addValueElement(pHandler, _XML_TAG_POSITION_ID, positionId);
      }
      UtilXml.endElement(pHandler, _XML_TAG_POSITION_IDS);
    }
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
    pByteList.addByte((byte) getSlots());
    pByteList.addStringArray(getPositionIds());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    UtilDialogParameter.validateDialogId(this, new DialogIdFactory().forId(pByteArray.getByte()));
    fTeamId = pByteArray.getString();
    fSlots = pByteArray.getByte();
    addPositionIds(pByteArray.getStringArray());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    IJsonOption.SLOTS.addTo(jsonObject, fSlots);
    IJsonOption.POSITION_IDS.addTo(jsonObject, getPositionIds());
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    fSlots = IJsonOption.SLOTS.getFrom(jsonObject);
    addPositionIds(IJsonOption.POSITION_IDS.getFrom(jsonObject));
  }

}
