package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ReportRaiseDead implements IReport {
  
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_NURGLES_ROT = "nurglesRot";

  private String fPlayerId;
  private boolean fNurglesRot;
  
  public ReportRaiseDead() {
    super();
  }

  public ReportRaiseDead(String pPlayerId, boolean pNurglesRot) {
    fPlayerId = pPlayerId;
    fNurglesRot = pNurglesRot;    
  }
  
  public ReportId getId() {
    return ReportId.RAISE_DEAD;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public boolean isNurglesRot() {
    return fNurglesRot;
  }

  // transformation
  
  public IReport transform() {
    return new ReportRaiseDead(getPlayerId(), isNurglesRot());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NURGLES_ROT, isNurglesRot());
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
    pByteList.addBoolean(isNurglesRot());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fNurglesRot = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
    
}
