package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRollSourceFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;





/**
 * 
 * @author Kalimar
 */
public class ReportReRoll implements IReport {
  
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_RE_ROLL_SOURCE = "reRollSource";
  private static final String _XML_ATTRIBUTE_SUCCESSFUL = "successful";
  private static final String _XML_ATTRIBUTE_ROLL = "roll";

  private String fPlayerId;
  private ReRollSource fReRollSource;
  private boolean fSuccessful;
  private int fRoll;
  
  public ReportReRoll() {
    super();
  }

  public ReportReRoll(String pPlayerId, ReRollSource pReRollSource, boolean pSuccessful, int pRoll) {
    fPlayerId = pPlayerId;
    fReRollSource = pReRollSource;    
    fSuccessful = pSuccessful;
    fRoll = pRoll;
  }
  
  public ReportId getId() {
    return ReportId.RE_ROLL;
  }

  public String getPlayerId() {
    return fPlayerId;
  }
  
  public ReRollSource getReRollSource() {
    return fReRollSource;
  }
  
  public boolean isSuccessful() {
    return fSuccessful;
  }
  
  public int getRoll() {
    return fRoll;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportReRoll(getPlayerId(), getReRollSource(), isSuccessful(), getRoll());
  }
    
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_RE_ROLL_SOURCE, (getReRollSource() != null) ? getReRollSource().getName() : null);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SUCCESSFUL, isSuccessful());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL, getRoll());
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
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getPlayerId());
    pByteList.addByte((byte) ((getReRollSource() != null) ? getReRollSource().getId() : 0));
    pByteList.addBoolean(isSuccessful());
    pByteList.addByte((byte) getRoll());
  }

  public int initFrom(ByteArray pByteArray) {
    ReportId reportId = ReportId.fromId(pByteArray.getSmallInt());
    if (getId() != reportId) {
      throw new IllegalStateException("Wrong report id. Expected " + getId().getName() + " received " + ((reportId != null) ? reportId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fReRollSource = new ReRollSourceFactory().forId(pByteArray.getByte());
    fSuccessful = pByteArray.getBoolean();
    fRoll = pByteArray.getByte();
    return byteArraySerializationVersion;
  }
      
}
