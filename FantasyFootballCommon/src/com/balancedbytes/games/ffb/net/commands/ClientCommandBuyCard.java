package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.CardType;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;




/**
 * 
 * @author Kalimar
 */
public class ClientCommandBuyCard extends NetCommand {
  
  private static final String _XML_ATTRIBUTE_TYPE = "type";

  private CardType fType;
  
  public ClientCommandBuyCard() {
    super();
  }
  
  public ClientCommandBuyCard(CardType pType) {
    fType = pType;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_BUY_CARD;
  }
  
  public CardType getType() {
	  return fType;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TYPE, (getType() != null) ? getType().getName() : null);
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
    pByteList.addByte((byte) ((getType() != null) ? getType().getId() : 0));
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fType = CardType.fromId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }

}
