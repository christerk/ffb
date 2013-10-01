package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import com.balancedbytes.games.ffb.Pushback;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ClientCommandPushback extends NetCommand {
  
  private Pushback fPushback;
  
  public ClientCommandPushback() {
    super();
  }

  public ClientCommandPushback(Pushback pPushback) {
    if (pPushback == null) {
      throw new IllegalArgumentException("Parameter pushback must not be null.");
    }
    fPushback = pPushback;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_PUSHBACK;
  }
  
  public Pushback getPushback() {
    return fPushback;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    UtilXml.startElement(pHandler, getId().getName());
    if (getPushback() != null) {
      getPushback().addToXml(pHandler);
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
    getPushback().addTo(pByteList);    
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPushback = new Pushback();
    fPushback.initFrom(pByteArray);
    return byteArraySerializationVersion;
  }

}
