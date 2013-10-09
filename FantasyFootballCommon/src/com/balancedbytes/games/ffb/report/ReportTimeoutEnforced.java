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
public class ReportTimeoutEnforced implements IReport {
  
  private static final String _XML_ATTRIBUTE_COACH = "coach";
  
  private String fCoach;
  
  public ReportTimeoutEnforced() {
    super();
  }
  
  public ReportTimeoutEnforced(String pCoach) {
    fCoach = pCoach;
  }

  public ReportId getId() {
    return ReportId.TIMEOUT_ENFORCED;
  }

  public String getCoach() {
    return fCoach;
  }

  // transformation
  
  public IReport transform() {
    return new ReportTimeoutEnforced(getCoach());
  }

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_COACH, getCoach());
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
    pByteList.addString(getCoach());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fCoach = pByteArray.getString();
    return byteArraySerializationVersion;
  }
    
}
