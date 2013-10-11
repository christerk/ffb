package com.balancedbytes.games.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.PlayerChoiceMode;
import com.balancedbytes.games.ffb.PlayerChoiceModeFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ClientCommandPlayerChoice extends NetCommand {
  
  private PlayerChoiceMode fPlayerChoiceMode;
  private List<String> fPlayerIds;
  
  public ClientCommandPlayerChoice() {
    fPlayerIds = new ArrayList<String>();
  }

  public ClientCommandPlayerChoice(PlayerChoiceMode pPlayerChoiceMode, Player[] pPlayers) {
    this();
    fPlayerChoiceMode = pPlayerChoiceMode;
    if (ArrayTool.isProvided(pPlayers)) {
      for (Player player : pPlayers) {
        addPlayerId(player.getId());
      }
    }
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_PLAYER_CHOICE;
  }
  
  public String getPlayerId() {
    return ((fPlayerIds.size() > 0) ? fPlayerIds.get(0) : null);
  }
  
  public String[] getPlayerIds() {
    return fPlayerIds.toArray(new String[fPlayerIds.size()]);
  }
  
  public void addPlayerId(String pPlayerId) {
    if (StringTool.isProvided(pPlayerId)) {
      fPlayerIds.add(pPlayerId);
    }
  }
  
  private void addPlayerIds(String[] pPlayerIds) {
    if (ArrayTool.isProvided(pPlayerIds)) {
      for (int i = 0; i < pPlayerIds.length; i++) {
        addPlayerId(pPlayerIds[i]);
      }
    }
  }

  public PlayerChoiceMode getPlayerChoiceMode() {
    return fPlayerChoiceMode;
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) ((getPlayerChoiceMode() != null) ? getPlayerChoiceMode().getId() : 0));
    pByteList.addStringArray(getPlayerIds());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerChoiceMode = new PlayerChoiceModeFactory().forId(pByteArray.getByte());
    addPlayerIds(pByteArray.getStringArray());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_CHOICE_MODE.addTo(jsonObject, fPlayerChoiceMode);
    IJsonOption.PLAYER_IDS.addTo(jsonObject, fPlayerIds);
    return jsonObject;
  }

  public ClientCommandPlayerChoice initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fPlayerChoiceMode = (PlayerChoiceMode) IJsonOption.PLAYER_CHOICE_MODE.getFrom(jsonObject);
    addPlayerIds(IJsonOption.PLAYER_IDS.getFrom(jsonObject));
    return this;
  }

}
