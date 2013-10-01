package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ClientCommandReplay extends NetCommand {
  
  private static final String _XML_ATTRIBUTE_GAME_ID = "gameId";
  private static final String _XML_ATTRIBUTE_REPLAY_TO_COMMAND_NR = "replayToCommandNr";
  
  private long fGameId;
  private int fReplayToCommandNr;
  
  public ClientCommandReplay() {
    super();
  }

  public ClientCommandReplay(long pGameId, int pReplayToCommandNr) {
    fGameId = pGameId;
    fReplayToCommandNr = pReplayToCommandNr;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_REPLAY;
  }
  
  public long getGameId() {
    return fGameId;
  }

  public int getReplayToCommandNr() {
    return fReplayToCommandNr;
  }

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_GAME_ID, getGameId());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_REPLAY_TO_COMMAND_NR, getReplayToCommandNr());
    UtilXml.addEmptyElement(pHandler, getId().getName(), attributes);
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
    pByteList.addLong(getGameId());
    pByteList.addSmallInt(getReplayToCommandNr());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fGameId = pByteArray.getLong();
    fReplayToCommandNr = pByteArray.getSmallInt();
    return byteArraySerializationVersion;
  }
  
}
