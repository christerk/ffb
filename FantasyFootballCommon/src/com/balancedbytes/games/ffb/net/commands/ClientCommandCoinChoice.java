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
public class ClientCommandCoinChoice extends NetCommand {
  
  private static final String _XML_ATTRIBUTE_CHOICE_HEADS = "choiceHeads";
  
  private boolean fChoiceHeads;
  
  public ClientCommandCoinChoice() {
    super();
  }

  public ClientCommandCoinChoice(boolean pChoiceHeads) {
    fChoiceHeads = pChoiceHeads;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_COIN_CHOICE;
  }
  
  public boolean isChoiceHeads() {
    return fChoiceHeads;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CHOICE_HEADS, isChoiceHeads());
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
    pByteList.addBoolean(isChoiceHeads());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fChoiceHeads = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }

}
