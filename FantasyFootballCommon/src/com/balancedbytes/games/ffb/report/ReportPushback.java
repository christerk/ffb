package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.PushbackMode;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ReportPushback implements IReport {
  
  private static final String _XML_ATTRIBUTE_DEFENDER_ID = "defenderId";
  private static final String _XML_ATTRIBUTE_MODE = "mode";
  
  private String fDefenderId;
  private PushbackMode fMode;
  
  public ReportPushback() {
    super();
  }

  public ReportPushback(String pDefenderId, PushbackMode pMode) {
    this();
    fDefenderId = pDefenderId;
    fMode = pMode;
  }
  
  public ReportId getId() {
    return ReportId.PUSHBACK;
  }
  
  public String getDefenderId() {
    return fDefenderId;
  }
  
  public PushbackMode getMode() {
    return fMode;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportPushback(getDefenderId(), getMode());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_DEFENDER_ID, getDefenderId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MODE, (getMode() != null) ? getMode().getName() : null);
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
    pByteList.addString(getDefenderId());
    pByteList.addByte((byte) ((getMode() != null) ? getMode().getId() : 0));
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fDefenderId = pByteArray.getString();
    fMode = PushbackMode.fromId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }

}
