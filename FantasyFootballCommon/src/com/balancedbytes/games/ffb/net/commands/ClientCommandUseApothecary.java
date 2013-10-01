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
public class ClientCommandUseApothecary extends NetCommand {
  
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_APOTHECARY_USED = "apothecaryUsed";
  
  private String fPlayerId;
  private boolean fApothecaryUsed;
  
  public ClientCommandUseApothecary() {
    super();
  }
  
  public ClientCommandUseApothecary(String pPlayerId, boolean pApothecaryUsed) {
    fPlayerId = pPlayerId;
    fApothecaryUsed = pApothecaryUsed;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_USE_APOTHECARY;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public boolean isApothecaryUsed() {
    return fApothecaryUsed;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_APOTHECARY_USED, isApothecaryUsed());
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
    pByteList.addString(getPlayerId());
    pByteList.addBoolean(isApothecaryUsed());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fApothecaryUsed = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }

}
