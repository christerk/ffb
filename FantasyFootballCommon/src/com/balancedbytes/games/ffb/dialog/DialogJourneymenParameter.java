package com.balancedbytes.games.ffb.dialog;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DialogJourneymenParameter implements IDialogParameter {
  
  private String fTeamId;
  private int fNrOfSlots;
  private List<String> fPositionIds;

  public DialogJourneymenParameter() {
    fPositionIds = new ArrayList<String>();
  }
  
  public DialogJourneymenParameter(String pTeamId, int pNrOfSlots, String[] pPositionIds) {
    this();
    fTeamId = pTeamId;
    fNrOfSlots = pNrOfSlots;
    addPositionIds(pPositionIds);
  }
  
  public DialogId getId() {
    return DialogId.JOURNEYMEN;
  }
  
  public String getTeamId() {
    return fTeamId;
  }
  
  public int getNrOfSlots() {
    return fNrOfSlots;
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
    return new DialogJourneymenParameter(getTeamId(), getNrOfSlots(), getPositionIds());
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) getId().getId());
    pByteList.addString(getTeamId());
    pByteList.addByte((byte) getNrOfSlots());
    pByteList.addStringArray(getPositionIds());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    UtilDialogParameter.validateDialogId(this, new DialogIdFactory().forId(pByteArray.getByte()));
    fTeamId = pByteArray.getString();
    fNrOfSlots = pByteArray.getByte();
    addPositionIds(pByteArray.getStringArray());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    IJsonOption.NR_OF_SLOTS.addTo(jsonObject, fNrOfSlots);
    IJsonOption.POSITION_IDS.addTo(jsonObject, getPositionIds());
    return jsonObject;
  }
  
  public DialogJourneymenParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    fNrOfSlots = IJsonOption.NR_OF_SLOTS.getFrom(jsonObject);
    addPositionIds(IJsonOption.POSITION_IDS.getFrom(jsonObject));
    return this;
  }

}
