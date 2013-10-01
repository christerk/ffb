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
public class DialogReceiveChoiceParameter implements IDialogParameter {
  
  private static final String _XML_ATTRIBUTE_CHOOSING_TEAM_ID = "choosingTeamId";
  
  private String fChoosingTeamId;

  public DialogReceiveChoiceParameter() {
    super();
  }
  
  public DialogReceiveChoiceParameter(String pChoosingTeamId) {
    fChoosingTeamId = pChoosingTeamId;
  }
  
  public DialogId getId() {
    return DialogId.RECEIVE_CHOICE;
  }

  public String getChoosingTeamId() {
    return fChoosingTeamId;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogReceiveChoiceParameter(getChoosingTeamId());
  }

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CHOOSING_TEAM_ID, getChoosingTeamId());
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
    pByteList.addString(getChoosingTeamId());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    DialogId dialogId = DialogId.fromId(pByteArray.getByte());
    if (getId() != dialogId) {
      throw new IllegalStateException("Wrong dialog id. Expected " + getId().getName() + " received " + ((dialogId != null) ? dialogId.getName() : "null"));
    }
    fChoosingTeamId = pByteArray.getString();
    return byteArraySerializationVersion;
  }

}
