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
public class DialogWinningsReRollParameter implements IDialogParameter {
  
  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_ATTRIBUTE_OLD_ROLL = "oldRoll";
  
  private String fTeamId;
  private int fOldRoll;

  public DialogWinningsReRollParameter() {
    super();
  }
  
  public DialogWinningsReRollParameter(String pTeamId, int pOldRoll) {
    fTeamId = pTeamId;
    fOldRoll = pOldRoll;
  }
  
  public DialogId getId() {
    return DialogId.WINNINGS_RE_ROLL;
  }

  public String getTeamId() {
    return fTeamId;
  }
  
  public int getOldRoll() {
    return fOldRoll;
  }

  // transformation
  
  public IDialogParameter transform() {
    return new DialogWinningsReRollParameter(getTeamId(), getOldRoll());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_OLD_ROLL, getOldRoll());
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
    pByteList.addString(getTeamId());
    pByteList.addByte((byte) getOldRoll());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    DialogId dialogId = DialogId.fromId(pByteArray.getByte());
    if (getId() != dialogId) {
      throw new IllegalStateException("Wrong dialog id. Expected " + getId().getName() + " received " + ((dialogId != null) ? dialogId.getName() : "null"));
    }
    fTeamId = pByteArray.getString();
    fOldRoll = pByteArray.getByte();
    return byteArraySerializationVersion;
  }

}
