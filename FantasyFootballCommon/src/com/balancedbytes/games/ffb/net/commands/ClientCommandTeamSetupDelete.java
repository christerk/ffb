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
public class ClientCommandTeamSetupDelete extends NetCommand {
  
  private static final String _XML_ATTRIBUTE_SETUP_NAME = "setupName";
  
  private String fSetupName;
  
  public ClientCommandTeamSetupDelete() {
    super();
  }

  public ClientCommandTeamSetupDelete(String pSetupName) {
    fSetupName = pSetupName;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_TEAM_SETUP_DELETE;
  }
  
  public String getSetupName() {
    return fSetupName;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SETUP_NAME, getSetupName());
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
    pByteList.addString(getSetupName());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fSetupName = pByteArray.getString();
    return byteArraySerializationVersion;
  }

}
