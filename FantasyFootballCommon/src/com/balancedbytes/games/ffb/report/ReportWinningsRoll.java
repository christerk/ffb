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
public class ReportWinningsRoll implements IReport {
  
  private static final String _XML_ATTRIBUTE_ROLL_HOME = "rollHome";
  private static final String _XML_ATTRIBUTE_WINNINGS_HOME = "winningsHome";
  private static final String _XML_ATTRIBUTE_ROLL_AWAY = "rollAway";
  private static final String _XML_ATTRIBUTE_WINNINGS_AWAY = "winningsAway";

  private int fRollHome;
  private int fWinningsHome;
  private int fRollAway;
  private int fWinningsAway;
  
  public ReportWinningsRoll() {
    super();
  }

  public ReportWinningsRoll(int pRollHome, int pWinningsHome, int pRollAway, int pWinningsAway) {
    fRollHome = pRollHome;
    fWinningsHome = pWinningsHome;
    fRollAway = pRollAway;
    fWinningsAway = pWinningsAway;
  }
  
  public ReportId getId() {
    return ReportId.WINNINGS_ROLL;
  }

  public int getRollHome() {
    return fRollHome;
  }

  public int getWinningsHome() {
    return fWinningsHome;
  }

  public int getRollAway() {
    return fRollAway;
  }

  public int getWinningsAway() {
    return fWinningsAway;
  }

  // transformation
  
  public IReport transform() {
    return new ReportWinningsRoll(getRollAway(), getWinningsAway(), getRollHome(), getWinningsHome());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL_HOME, getRollHome());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_WINNINGS_HOME, getWinningsHome());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL_AWAY, getRollAway());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_WINNINGS_AWAY, getWinningsAway());
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
    pByteList.addInt(getWinningsHome());
    pByteList.addByte((byte) getRollAway());
    pByteList.addInt(getWinningsAway());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fRollHome = pByteArray.getByte();
    fWinningsHome = pByteArray.getInt();
    fRollAway = pByteArray.getByte();
    fWinningsAway = pByteArray.getInt();
    return byteArraySerializationVersion;
  }
    
}
