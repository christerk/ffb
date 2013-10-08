package com.balancedbytes.games.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

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
public class ClientCommandJourneymen extends NetCommand {

  private static final String _XML_TAG_JOURNEYMAN = "journeyman";
  private static final String _XML_ATTRIBUTE_POSITION_ID = "positionId";
  private static final String _XML_ATTRIBUTE_SLOTS = "slots";

  private List<Integer> fSlots;
  private List<String> fPositionIds;

  public ClientCommandJourneymen() {
    fSlots = new ArrayList<Integer>();
    fPositionIds = new ArrayList<String>();
  }

  public ClientCommandJourneymen(String[] pPositionsIds, int[] pSlots) {
    this();
    addPositionIds(pPositionsIds);
    addSlots(pSlots);
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_JOURNEYMEN;
  }

  public String[] getPositionIds() {
    return fPositionIds.toArray(new String[fPositionIds.size()]);
  }

  public int[] getSlots() {
    int[] slots = new int[fSlots.size()];
    for (int i = 0; i < slots.length; i++) {
      slots[i] = fSlots.get(i);
    }
    return slots;
  }

  public int getSlotsTotal() {
    int total = 0;
    int[] slots = getSlots();
    for (int i = 0; i < slots.length; i++) {
      total += slots[i];
    }
    return total;
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

  private void addSlots(int[] pSlots) {
    if (ArrayTool.isProvided(pSlots)) {
      for (int slots : pSlots) {
        fSlots.add(slots);
      }
    }
  }

  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    UtilXml.startElement(pHandler, getId().getName());
    String[] positionIds = getPositionIds();
    int[] slots = getSlots();
    if (ArrayTool.isProvided(positionIds) && ArrayTool.isProvided(slots)) {
      for (int i = 0; i < positionIds.length; i++) {
        AttributesImpl attributes = new AttributesImpl();
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_POSITION_ID, positionIds[i]);
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SLOTS, slots[i]);
        UtilXml.addEmptyElement(pHandler, _XML_TAG_JOURNEYMAN, attributes);
      }
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
    pByteList.addStringArray(getPositionIds());
    pByteList.addByteArray(getSlots());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    addPositionIds(pByteArray.getStringArray());
    addSlots(pByteArray.getByteArrayAsIntArray());
    return byteArraySerializationVersion;
  }

  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.POSITION_IDS.addTo(jsonObject, fPositionIds);
    IJsonOption.SLOTS.addTo(jsonObject, fSlots);
    return jsonObject;
  }

  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    addPositionIds(IJsonOption.POSITION_IDS.getFrom(jsonObject));
    addSlots(IJsonOption.SLOTS.getFrom(jsonObject));
  }
}
