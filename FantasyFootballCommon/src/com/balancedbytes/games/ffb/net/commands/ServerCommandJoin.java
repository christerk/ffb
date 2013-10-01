package com.balancedbytes.games.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.UtilXml;


/**
 * 
 * @author Kalimar
 */
public class ServerCommandJoin extends ServerCommand {

  private static final String _XML_ATTRIBUTE_COACH = "coach";
  private static final String _XML_ATTRIBUTE_MODE = "mode";
  private static final String _XML_ATTRIBUTE_SPECTATORS = "spectators";
  
  private static final String _XML_TAG_PLAYER_LIST = "playerList";
  private static final String _XML_TAG_PLAYER = "player";
  
  private String fCoach;
  private ClientMode fMode;
  private List<String> fPlayers;
  private int fSpectators;
  
  public ServerCommandJoin() {
    fPlayers = new ArrayList<String>();
  }
  
  public ServerCommandJoin(String pCoach, ClientMode pMode, String[] pPlayers, int pSpectators) {
    this();
    fCoach = pCoach;
    fMode = pMode;
    addPlayers(pPlayers);
    fSpectators = pSpectators;
  }
  
  public NetCommandId getId() {
    return NetCommandId.SERVER_JOIN;
  }

  public String getCoach() {
    return fCoach;
  }
  
  public ClientMode getMode() {
    return fMode;
  }
  
  public String[] getPlayers() {
    return fPlayers.toArray(new String[fPlayers.size()]);
  }
  
  private void addPlayer(String pPlayer) {
    if (StringTool.isProvided(pPlayer)) {
      fPlayers.add(pPlayer);
    }
  }
  
  private void addPlayers(String[] pPlayers) {
    if (ArrayTool.isProvided(pPlayers)) {
      for (String player : pPlayers) {
        addPlayer(player);
      }
    }
  }
  
  public int getSpectators() {
    return fSpectators;
  }

  public boolean isReplayable() {
    return false;
  }

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    if (getCommandNr() > 0) {
      UtilXml.addAttribute(attributes, XML_ATTRIBUTE_COMMAND_NR, getCommandNr());
    }
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_COACH, getCoach());
    String modeName = (getMode() != null) ? getMode().getName() : null;
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MODE, modeName);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SPECTATORS, getSpectators());
    UtilXml.startElement(pHandler, getId().getName(), attributes);
    String[] players = getPlayers();
    if (ArrayTool.isProvided(players)) {
      UtilXml.startElement(pHandler, _XML_TAG_PLAYER_LIST);
      for (String player : players) {
        UtilXml.addValueElement(pHandler, _XML_TAG_PLAYER, player);
      }
      UtilXml.endElement(pHandler, _XML_TAG_PLAYER_LIST);
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
    pByteList.addString(getCoach());
    pByteList.addByte((byte) getMode().getId());
    pByteList.addSmallInt(getSpectators());
    pByteList.addStringArray(getPlayers());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setCommandNr(pByteArray.getSmallInt());
    fCoach = pByteArray.getString();
    fMode = ClientMode.fromId(pByteArray.getByte());
    fSpectators = pByteArray.getSmallInt();
    addPlayers(pByteArray.getStringArray());
    return byteArraySerializationVersion;
  }
    
}
