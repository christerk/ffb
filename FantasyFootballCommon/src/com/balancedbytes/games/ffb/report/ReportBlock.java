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
public class ReportBlock implements IReport {

  private static final String _XML_ATTRIBUTE_DEFENDER_ID = "defenderId";
  
  private String fDefenderId;
  
  public ReportBlock() {
    super();
  }
  
  public ReportBlock(String pDefenderId) {
    fDefenderId = pDefenderId;
  }
  
  public ReportId getId() {
    return ReportId.BLOCK;
  }
  
  public String getDefenderId() {
    return fDefenderId;
  }
    
  // transformation
  
  public IReport transform() {
    return new ReportBlock(getDefenderId());
  }
    
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_DEFENDER_ID, getDefenderId());    
    UtilXml.addEmptyElement(pHandler, XML_TAG, attributes);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 2;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getDefenderId());
  }
  
  public int initFrom(ByteArray pByteArray) {
    ReportId reportId = ReportId.fromId(pByteArray.getSmallInt());
    if (getId() != reportId) {
      throw new IllegalStateException("Wrong report id. Expected " + getId().getName() + " received " + ((reportId != null) ? reportId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fDefenderId = pByteArray.getString();
    if (byteArraySerializationVersion < 2) {
    	pByteArray.getBoolean();  // deprecated flag usingHorns
    }
    return byteArraySerializationVersion;
  }

}
