package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.UtilXml;




/**
 * 
 * @author Kalimar
 */
public class ReportFumbblResultUpload implements IReport {
  
  private static final String _XML_ATTRIBUTE_SUCCESSFUL = "successful";
  private static final String _XML_TAG_STATUS = "status";
  
  private boolean fSuccessful;
  private String fStatus;
  
  public ReportFumbblResultUpload() {
    super();
  }
  
  public ReportFumbblResultUpload(boolean pSuccessful, String pStatus) {
    fSuccessful = pSuccessful;
    fStatus = pStatus;
  }
  
  public ReportId getId() {
    return ReportId.FUMBBL_RESULT_UPLOAD;
  }
  
  public boolean isSuccessful() {
    return fSuccessful;
  }
  
  public String getStatus() {
    return fStatus;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportFumbblResultUpload(isSuccessful(), getStatus());
  }

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SUCCESSFUL, isSuccessful());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    if (StringTool.isProvided(getStatus())) {
      UtilXml.addValueElement(pHandler, _XML_TAG_STATUS, getStatus());
    }
    UtilXml.endElement(pHandler, XML_TAG);
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
    pByteList.addBoolean(isSuccessful());
    pByteList.addString(getStatus());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fSuccessful = pByteArray.getBoolean();
    fStatus = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
}
