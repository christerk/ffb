package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.SendToBoxReasonFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.net.NetCommandId;

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
    
}
