package com.balancedbytes.games.ffb.server.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.xml.UtilXml;


/**
 * 
 * @author Kalimar
 */
public abstract class InternalServerCommand extends NetCommand {
  
  protected static final String XML_ATTRIBUTE_GAME_ID = "gameId";
  
  private long fGameId;

  public InternalServerCommand(long pGameId) {
    setGameId(pGameId);
  }
  
  public long getGameId() {
    return fGameId;
  }
  
  protected void setGameId(long pGameId) {
    fGameId = pGameId;
  }
  
  public boolean isInternal() {
    return true;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    if (getGameId() > 0) {
      UtilXml.addAttribute(attributes, XML_ATTRIBUTE_GAME_ID, getGameId());
    }
    UtilXml.addEmptyElement(pHandler, getId().getName(), attributes);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 0;
  }
  
  public void addTo(ByteList pByteList) {
    // do nothing
  }
  
  public int initFrom(ByteArray pByteArray) {
    return getByteArraySerializationVersion();
  }
  
}
