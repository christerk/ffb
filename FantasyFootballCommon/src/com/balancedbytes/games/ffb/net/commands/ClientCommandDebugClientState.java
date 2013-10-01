package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ClientCommandDebugClientState extends NetCommand {
  
  private static final String _XML_ATTRIBUTE_CLIENT_STATE_ID = "clientStateId";
  
  private ClientStateId fClientStateId;
  
  public ClientCommandDebugClientState() {
    super();
  }

  public ClientCommandDebugClientState(ClientStateId pClientStateId) {
    this();
    fClientStateId = pClientStateId;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_DEBUG_CLIENT_STATE;
  }
  
  public ClientStateId getClientStateId() {
    return fClientStateId;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CLIENT_STATE_ID, (getClientStateId() != null) ? getClientStateId().getName() : null);
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
    pByteList.addByte((byte) ((getClientStateId() != null) ? getClientStateId().getId() : 0));
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fClientStateId = ClientStateId.fromId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }

}
