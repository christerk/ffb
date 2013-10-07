package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.SendToBoxReasonFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandAddPlayer extends ServerCommand {
  
  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";

  private static final String _XML_TAG_PLAYER_STATE = "playerState";
  
  private static final String _XML_TAG_SEND_TO_BOX = "sendToBox";
  private static final String _XML_ATTRIBUTE_REASON = "reason";
  private static final String _XML_ATTRIBUTE_TURN = "turn";
  private static final String _XML_ATTRIBUTE_HALF = "half";
  
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
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    if (getCommandNr() > 0) {
      UtilXml.addAttribute(attributes, XML_ATTRIBUTE_COMMAND_NR, getCommandNr());
    }
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.startElement(pHandler, getId().getName(), attributes);
    if (getPlayer() != null) {
      getPlayer().addToXml(pHandler);
    }
    if (getPlayerState() != null) {
      UtilXml.addValueElement(pHandler, _XML_TAG_PLAYER_STATE, getPlayerState().getId());
    }
    if (getSendToBoxReason() != null) {
      attributes = new AttributesImpl();
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_REASON, getSendToBoxReason().getName());
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TURN, getSendToBoxTurn());
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_HALF, getSendToBoxHalf());
      UtilXml.addEmptyElement(pHandler, _XML_TAG_SEND_TO_BOX, attributes);
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
