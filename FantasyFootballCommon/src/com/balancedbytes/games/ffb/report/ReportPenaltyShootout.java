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
public class ReportPenaltyShootout implements IReport {

  private static final String _XML_ATTRIBUTE_ROLL_HOME = "rollHome";
  private static final String _XML_ATTRIBUTE_RE_ROLLS_LEFT_HOME = "reRollsLeftHome";
  private static final String _XML_ATTRIBUTE_ROLL_AWAY = "rollAway";
  private static final String _XML_ATTRIBUTE_RE_ROLLS_LEFT_AWAY = "reRollsLeftAway";

  private int fRollHome;
  private int fReRollsLeftHome;
  private int fRollAway;
  private int fReRollsLeftAway;
  
  public ReportPenaltyShootout() {
    super();
  }
  
  public ReportPenaltyShootout(int pRollHome, int pReRollsLeftHome, int pRollAway, int pReRollsLeftAway) {
    fRollHome = pRollHome;
    fReRollsLeftHome = pReRollsLeftHome;
    fRollAway = pRollAway;
    fReRollsLeftAway = pReRollsLeftAway;
  }
  
  public ReportId getId() {
    return ReportId.PENALTY_SHOOTOUT;
  }

  public int getRollHome() {
    return fRollHome;
  }
  
  public int getReRollsLeftHome() {
    return fReRollsLeftHome;
  }
  
  public int getRollAway() {
    return fRollAway;
  }
  
  public int getReRollsLeftAway() {
    return fReRollsLeftAway;
  }

  // transformation
  
  public IReport transform() {
    return new ReportPenaltyShootout(getRollAway(), getReRollsLeftAway(), getRollHome(), getReRollsLeftHome());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL_HOME, getRollHome());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_RE_ROLLS_LEFT_HOME, getReRollsLeftHome());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL_AWAY, getRollAway());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_RE_ROLLS_LEFT_AWAY, getReRollsLeftAway());
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
    pByteList.addByte((byte) getRollHome());
    pByteList.addByte((byte) getReRollsLeftHome());
    pByteList.addByte((byte) getRollAway());
    pByteList.addByte((byte) getReRollsLeftAway());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fRollHome = pByteArray.getByte();
    fReRollsLeftHome = pByteArray.getByte();
    fRollAway = pByteArray.getByte();
    fReRollsLeftAway = pByteArray.getByte();
    return byteArraySerializationVersion;
  }
    
}
