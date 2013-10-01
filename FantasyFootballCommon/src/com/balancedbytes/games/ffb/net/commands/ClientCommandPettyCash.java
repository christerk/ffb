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
public class ClientCommandPettyCash extends NetCommand {
  
  private static final String _XML_ATTRIBUTE_PETTY_CASH = "pettyCash";

  private int fPettyCash;
  
  public ClientCommandPettyCash() {
    super();
  }
  
  public ClientCommandPettyCash(int pPettyCash) {
    fPettyCash = pPettyCash;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_PETTY_CASH;
  }
  
  public int getPettyCash() {
    return fPettyCash;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PETTY_CASH, getPettyCash());
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
    pByteList.addInt(getPettyCash());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPettyCash = pByteArray.getInt();
    return byteArraySerializationVersion;
  }

}
