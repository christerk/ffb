package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;




/**
 * 
 * @author Kalimar
 */
public class ServerCommandPing extends ServerCommand {

  private static final String _XML_ATTRIBUTE_TIMESTAMP = "timestamp";
  
  private long fTimestamp;
  private transient long fReceived;
  
  public ServerCommandPing() {
    super();
  }

  public ServerCommandPing(long pTimestamp) {
    fTimestamp = pTimestamp;
  }
 
  public NetCommandId getId() {
    return NetCommandId.SERVER_PING;
  }
  
  public long getTimestamp() {
    return fTimestamp;
  }
  
  public void setReceived(long pReceived) {
    fReceived = pReceived;
  }
  
  public long getReceived() {
    return fReceived;
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
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TIMESTAMP, getTimestamp());

  	UtilXml.startElement(pHandler, getId().getName(), attributes);
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
    pByteList.addLong(getTimestamp());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setCommandNr(pByteArray.getSmallInt());
    fTimestamp = pByteArray.getLong();
    return byteArraySerializationVersion;
  }
    
}
