package com.balancedbytes.games.ffb.dialog;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class DialogUseIgorParameter implements IDialogParameter {
  
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  
  private String fPlayerId;

  public DialogUseIgorParameter() {
    super();
  }
  
  public DialogUseIgorParameter(String pPlayerId) {
    fPlayerId = pPlayerId;
  }
  
  public DialogId getId() {
    return DialogId.USE_IGOR;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
    
  // transformation
  
  public IDialogParameter transform() {
    return new DialogUseIgorParameter(getPlayerId());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
    UtilXml.addEmptyElement(pHandler, XML_TAG, attributes);
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
    pByteList.addByte((byte) getId().getId());
    pByteList.addString(getPlayerId());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    DialogId dialogId = DialogId.fromId(pByteArray.getByte());
    if (getId() != dialogId) {
      throw new IllegalStateException("Wrong dialog id. Expected " + getId().getName() + " received " + ((dialogId != null) ? dialogId.getName() : "null"));
    }
    fPlayerId = pByteArray.getString();
    return byteArraySerializationVersion;
  }

}
