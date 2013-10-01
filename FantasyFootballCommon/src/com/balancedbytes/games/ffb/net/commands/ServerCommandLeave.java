package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;


/**
 * 
 * @author Kalimar
 */
public class ServerCommandLeave extends ServerCommand {

  private static final String _XML_ATTRIBUTE_COACH = "coach";
  private static final String _XML_ATTRIBUTE_MODE = "mode";
  private static final String _XML_ATTRIBUTE_SPECTATORS = "spectators";
  
  private String fCoach;
  private ClientMode fMode;
  private int fSpectators;
  
  public ServerCommandLeave() {
    super();
  }
  
  public ServerCommandLeave(String pCoach, ClientMode pMode, int pSpectators) {
    fCoach = pCoach;
    fMode = pMode;
    fSpectators = pSpectators;
  }
  
  public NetCommandId getId() {
    return NetCommandId.SERVER_LEAVE;
  }

  public String getCoach() {
    return fCoach;
  }
  
  public ClientMode getMode() {
    return fMode;
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
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MODE, (getMode() != null) ? getMode().getName() : null);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SPECTATORS, getSpectators());
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
    pByteList.addString(getCoach());
    pByteList.addByte((byte) getMode().getId());
    pByteList.addSmallInt(getSpectators());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setCommandNr(pByteArray.getSmallInt());
    fCoach = pByteArray.getString();
    fMode = ClientMode.fromId(pByteArray.getByte());
    fSpectators = pByteArray.getSmallInt();
    return byteArraySerializationVersion;
  }
  
}
