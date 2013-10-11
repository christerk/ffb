package com.balancedbytes.games.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ClientCommandTeamSetupSave extends NetCommand {
  
  private String fSetupName;
  private List<Integer> fPlayerNumbers;
  private List<FieldCoordinate> fPlayerCoordinates;
  
  public ClientCommandTeamSetupSave() {
    fPlayerNumbers = new ArrayList<Integer>();
    fPlayerCoordinates = new ArrayList<FieldCoordinate>();
  }

  public ClientCommandTeamSetupSave(String pSetupName, int[] pPlayerNumbers, FieldCoordinate[] pPlayerCoordinates) {
    this();
    fSetupName = pSetupName;
    addPlayerNumbers(pPlayerNumbers);
    addPlayerCoordinates(pPlayerCoordinates);
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_TEAM_SETUP_SAVE;
  }
  
  public String getSetupName() {
    return fSetupName;
  }
  
  public int[] getPlayerNumbers() {
    int[] playerNumbers = new int[fPlayerNumbers.size()];
    for (int i = 0; i < playerNumbers.length; i++) {
      playerNumbers[i] = fPlayerNumbers.get(i);
    }
    return playerNumbers;
  }
  
  private void addPlayerNumber(int pPlayerNumber) {
    fPlayerNumbers.add(pPlayerNumber);
  }

  private void addPlayerNumbers(int[] pPlayerNumbers) {
    if (ArrayTool.isProvided(pPlayerNumbers)) {
      for (int i = 0; i < pPlayerNumbers.length; i++) {
        addPlayerNumber(pPlayerNumbers[i]);
      }
    }
  }
  
  public FieldCoordinate[] getPlayerCoordinates() {
    return fPlayerCoordinates.toArray(new FieldCoordinate[fPlayerCoordinates.size()]);
  }
  
  private void addPlayerCoordinate(FieldCoordinate pPlayerCoordinate) {
    if (pPlayerCoordinate != null) {
      fPlayerCoordinates.add(pPlayerCoordinate);
    }
  }
  
  private void addPlayerCoordinates(FieldCoordinate[] pPlayerCoordinates) {
    if (ArrayTool.isProvided(pPlayerCoordinates)) {
      for (FieldCoordinate playerCoordinate : pPlayerCoordinates) {
        addPlayerCoordinate(playerCoordinate);
      }
    }
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getSetupName());
    pByteList.addByteArray(getPlayerNumbers());
    pByteList.addByte((byte) getPlayerCoordinates().length);
    for (int i = 0; i < getPlayerCoordinates().length; i++) {
      pByteList.addFieldCoordinate(getPlayerCoordinates()[i]);
    }
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fSetupName = pByteArray.getString();
    addPlayerNumbers(pByteArray.getByteArrayAsIntArray());
    int nrOfPlayerCoordinates = pByteArray.getByte();
    for (int i = 0; i < nrOfPlayerCoordinates; i++) {
      addPlayerCoordinate(pByteArray.getFieldCoordinate());
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.SETUP_NAME.addTo(jsonObject, fSetupName);
    IJsonOption.PLAYER_NUMBERS.addTo(jsonObject, fPlayerNumbers);
    IJsonOption.PLAYER_COORDINATES.addTo(jsonObject, fPlayerCoordinates);
    return jsonObject;
  }

  public ClientCommandTeamSetupSave initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fSetupName = IJsonOption.SETUP_NAME.getFrom(jsonObject);
    addPlayerNumbers(IJsonOption.PLAYER_NUMBERS.getFrom(jsonObject));
    addPlayerCoordinates(IJsonOption.PLAYER_COORDINATES.getFrom(jsonObject));
    return this;
  }

}
