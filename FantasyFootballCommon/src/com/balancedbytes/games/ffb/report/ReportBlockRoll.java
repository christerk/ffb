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
public class ReportBlockRoll implements IReport {
  
  private static final String _XML_ATTRIBUTE_ROLL = "roll";
  private static final String _XML_ATTRIBUTE_CHOOSING_TEAM_ID = "choosingTeamId";
  
  private int[] fRoll;
  private String fChoosingTeamId;
  
  public ReportBlockRoll() {
    super();
  }

  public ReportBlockRoll(String pChoosingTeamId, int[] pRoll) {
    fChoosingTeamId = pChoosingTeamId;
    fRoll = pRoll;
  }
  
  public ReportId getId() {
    return ReportId.BLOCK_ROLL;
  }
  
  public String getChoosingTeamId() {
    return fChoosingTeamId;
  }
  
  public int[] getRoll() {
    return fRoll;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportBlockRoll(getChoosingTeamId(), getRoll());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CHOOSING_TEAM_ID, getChoosingTeamId());
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
    pByteList.addString(getChoosingTeamId());
    pByteList.addByteArray(getRoll());
  }

  public int initFrom(ByteArray pByteArray) {
    ReportId reportId = ReportId.fromId(pByteArray.getSmallInt());
    if (getId() != reportId) {
      throw new IllegalStateException("Wrong report id. Expected " + getId().getName() + " received " + ((reportId != null) ? reportId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fChoosingTeamId = pByteArray.getString();
    fRoll = pByteArray.getByteArrayAsIntArray();
    return byteArraySerializationVersion;
  }
    
}
