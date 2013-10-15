package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.SendToBoxReasonFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandAddPlayer extends ServerCommand {
  
  private String fTeamId;
  private Player fPlayer;
  private PlayerState fPlayerState;
  private SendToBoxReason fSendToBoxReason;
  private int fSendToBoxTurn;
  private int fSendToBoxHalf;
  
  public ServerCommandAddPlayer() {
    super();
  }

  public ServerCommandAddPlayer(String pTeamId, Player pPlayer, PlayerState pPlayerState, PlayerResult pPlayerResult) {
    this();
    if (pPlayer == null) {
      throw new IllegalArgumentException("Parameter player must not be null.");
    }
    fTeamId = pTeamId;
    fPlayer = pPlayer;
    fPlayerState = pPlayerState;
    if (pPlayerResult != null) {
      fSendToBoxReason = pPlayerResult.getSendToBoxReason();
      fSendToBoxTurn = pPlayerResult.getSendToBoxTurn();
      fSendToBoxHalf = pPlayerResult.getSendToBoxHalf();
    }
  }
  
  public NetCommandId getId() {
    return NetCommandId.SERVER_ADD_PLAYER;
  }
  
  public String getTeamId() {
    return fTeamId;
  }

  public Player getPlayer() {
    return fPlayer;
  }
  
  public PlayerState getPlayerState() {
    return fPlayerState;
  }
  
  public SendToBoxReason getSendToBoxReason() {
    return fSendToBoxReason;
  }
  
  public int getSendToBoxHalf() {
    return fSendToBoxHalf;
  }
  
  public int getSendToBoxTurn() {
    return fSendToBoxTurn;
  }  
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addSmallInt(getCommandNr());
    pByteList.addString(getTeamId());
    getPlayer().addTo(pByteList);
    pByteList.addSmallInt((getPlayerState() != null) ? getPlayerState().getId() : 0); 
    pByteList.addByte((byte) ((getSendToBoxReason() != null) ? getSendToBoxReason().getId() : 0));
    pByteList.addByte((byte) getSendToBoxTurn());
    pByteList.addByte((byte) getSendToBoxHalf());
  }
    
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setCommandNr(pByteArray.getSmallInt());
    fTeamId = pByteArray.getString();
    fPlayer = new Player();
    fPlayer.initFrom(pByteArray);
    fPlayerState = new PlayerState(pByteArray.getSmallInt());
    fSendToBoxReason = new SendToBoxReasonFactory().forId(pByteArray.getByte());
    fSendToBoxTurn = pByteArray.getByte();
    fSendToBoxHalf = pByteArray.getByte();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    if (fPlayer != null) {
      IJsonOption.PLAYER.addTo(jsonObject, fPlayer.toJsonValue());
    }
    IJsonOption.PLAYER_STATE.addTo(jsonObject, fPlayerState);
    IJsonOption.SEND_TO_BOX_REASON.addTo(jsonObject, fSendToBoxReason);
    IJsonOption.SEND_TO_BOX_TURN.addTo(jsonObject, fSendToBoxTurn);
    IJsonOption.SEND_TO_BOX_HALF.addTo(jsonObject, fSendToBoxHalf);
    return jsonObject;
  }
  
  public ServerCommandAddPlayer initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    setCommandNr(IJsonOption.COMMAND_NR.getFrom(jsonObject));
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    JsonObject playerObject = IJsonOption.PLAYER.getFrom(jsonObject);
    if (playerObject != null) {
      fPlayer = new Player().initFrom(playerObject);
    }
    fPlayerState = IJsonOption.PLAYER_STATE.getFrom(jsonObject);
    fSendToBoxReason = (SendToBoxReason) IJsonOption.SEND_TO_BOX_REASON.getFrom(jsonObject);
    fSendToBoxTurn = IJsonOption.SEND_TO_BOX_TURN.getFrom(jsonObject);
    fSendToBoxHalf = IJsonOption.SEND_TO_BOX_HALF.getFrom(jsonObject);
    return this;
  }
    
}
