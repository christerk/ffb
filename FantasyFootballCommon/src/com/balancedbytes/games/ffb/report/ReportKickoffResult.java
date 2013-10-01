package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.KickoffResult;
import com.balancedbytes.games.ffb.KickoffResultFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;


/**
 * 
 * @author Kalimar
 */
public class ReportKickoffResult implements IReport {
  
  private static final String _XML_ATTRIBUTE_KICKOFF_RESULT = "kickoffResult";
  private static final String _XML_ATTRIBUTE_KICKOFF_ROLL = "kickoffRoll";
  
  private KickoffResult fKickoffResult;
  private int[] fKickoffRoll;
  
  public ReportKickoffResult() {
    super();
  }
  
  public ReportKickoffResult(
    KickoffResult pKickoffResult,
    int[] pKickoffRoll
  ) {
    fKickoffResult = pKickoffResult;
    fKickoffRoll = pKickoffRoll;
  }
  
  public ReportId getId() {
    return ReportId.KICKOFF_RESULT;
  }
  
  public KickoffResult getKickoffResult() {
    return fKickoffResult;
  }
  
  public int[] getKickoffRoll() {
    return fKickoffRoll;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportKickoffResult(getKickoffResult(), getKickoffRoll());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_KICKOFF_RESULT, (getKickoffResult() != null) ? getKickoffResult().getName() : null);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_KICKOFF_ROLL, getKickoffRoll());
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
    pByteList.addByte((byte) ((getKickoffResult() != null) ? getKickoffResult().getId() : 0));
    pByteList.addByteArray(getKickoffRoll());
  }
  
  public int initFrom(ByteArray pByteArray) {
    ReportId reportId = ReportId.fromId(pByteArray.getSmallInt());
    if (getId() != reportId) {
      throw new IllegalStateException("Wrong report id. Expected " + getId().getName() + " received " + ((reportId != null) ? reportId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fKickoffResult = new KickoffResultFactory().forId((int) pByteArray.getByte());
    fKickoffRoll = pByteArray.getByteArrayAsIntArray();
    return byteArraySerializationVersion;
  }

}
