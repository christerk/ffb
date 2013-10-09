package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.InducementTypeFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class ReportInducement implements IReport {

  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_ATTRIBUTE_TYPE = "type";
  private static final String _XML_ATTRIBUTE_VALUE = "value";

  private String fTeamId;
  private InducementType fType;
  private int fValue;

  public ReportInducement() {
    super();
  }

  public ReportInducement(String pTeamId, InducementType pType, int pValue) {
    fTeamId = pTeamId;
    fType = pType;
    fValue = pValue;
  }

  public ReportId getId() {
    return ReportId.INDUCEMENT;
  }
  
  public String getTeamId() {
    return fTeamId;
  }
  
  public InducementType getType() {
    return fType;
  }
  
  public int getValue() {
    return fValue;
  }

  // transformation

  public IReport transform() {
    return new ReportInducement(getTeamId(), getType(), getValue());
  }

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TYPE, (getType() != null) ? getType().getName() : null);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_VALUE, getValue());
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
    pByteList.addString(getTeamId());
    pByteList.addByte((byte) ((getType() != null) ? getType().getId() : 0));
    pByteList.addByte((byte) getValue());
  }

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTeamId = pByteArray.getString();
    fType = new InducementTypeFactory().forId(pByteArray.getByte());
    fValue = pByteArray.getByte();
    return byteArraySerializationVersion;
  }

}
