package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ServerCommandStatus extends ServerCommand {
  
  private static final String _XML_ATTRIBUTE_STATUS = "status";
  private static final String _XML_ATTRIBUTE_MESSAGE = "message";
  
  private ServerStatus fStatus;
  private String fMessage;
  
  public ServerCommandStatus() {
    super();
  }
  
  public ServerCommandStatus(ServerStatus pStatus, String pMessage) {
    fStatus = pStatus;
    fMessage = pMessage;
  }
  
  public NetCommandId getId() {
    return NetCommandId.SERVER_STATUS;
  }

  public ServerStatus getStatus() {
    return fStatus;
  }
  
  public String getMessage() {
    return fMessage;
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
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_STATUS, (getStatus() != null) ? getStatus().getName() : null);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MESSAGE, getMessage());
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
    pByteList.addSmallInt(getCommandNr());
    if (getStatus() != null) {
      pByteList.addByte((byte) getStatus().getId());
    } else {
      pByteList.addByte((byte) 0);
    }
    pByteList.addString(getMessage());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setCommandNr(pByteArray.getSmallInt());
    fStatus = ServerStatus.fromId(pByteArray.getByte());
    fMessage = pByteArray.getString();
    return byteArraySerializationVersion;
  }

 }
