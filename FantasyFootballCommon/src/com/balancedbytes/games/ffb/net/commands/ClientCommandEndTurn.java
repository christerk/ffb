package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ClientCommandEndTurn extends NetCommand {
	
  public ClientCommandEndTurn() {
    super();
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_END_TURN;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    UtilXml.addEmptyElement(pHandler, getId().getName());
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 3;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    if (byteArraySerializationVersion == 2) {
    	pByteArray.getByte(); // deprecated
    }
    return byteArraySerializationVersion;
  }
      
}
