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
public class ClientCommandReceiveChoice extends NetCommand {
  
  private static final String _XML_ATTRIBUTE_CHOICE_RECEIVE = "choiceReceive";
  
  private boolean fChoiceReceive;
  
  public ClientCommandReceiveChoice() {
    super();
  }

  public ClientCommandReceiveChoice(boolean pChoiceReceive) {
    fChoiceReceive = pChoiceReceive;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_RECEIVE_CHOICE;
  }
  
  public boolean isChoiceReceive() {
    return fChoiceReceive;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CHOICE_RECEIVE, isChoiceReceive());
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
    pByteList.addBoolean(isChoiceReceive());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fChoiceReceive = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }

}
