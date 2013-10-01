package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.ConcedeGameStatus;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ClientCommandConcedeGame extends NetCommand {
  
  private static final String _XML_ATTRIBUTE_STATUS = "status";
  
  private ConcedeGameStatus fStatus;
  
  public ClientCommandConcedeGame() {
    super();
  }

  public ClientCommandConcedeGame(ConcedeGameStatus pStatus) {
    fStatus = pStatus;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_CONCEDE_GAME;
  }
  
  public ConcedeGameStatus getStatus() {
    return fStatus;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_STATUS, (getStatus() != null) ? getStatus().getName() : null);
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
    pByteList.addByte((byte) ((getStatus() != null) ? getStatus().getId() : 0));
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fStatus = ConcedeGameStatus.fromId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }
    
}
