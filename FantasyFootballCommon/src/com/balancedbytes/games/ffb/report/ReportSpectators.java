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
public class ReportSpectators implements IReport {
  
  private static final String _XML_ATTRIBUTE_ROLL_HOME = "rollHome";
  private static final String _XML_ATTRIBUTE_SPECTATORS_HOME = "spectatorsHome";
  private static final String _XML_ATTRIBUTE_FAME_HOME = "fameHome";
  private static final String _XML_ATTRIBUTE_ROLL_AWAY = "rollAway";
  private static final String _XML_ATTRIBUTE_SPECTATORS_AWAY = "spectatorsAway";
  private static final String _XML_ATTRIBUTE_FAME_AWAY = "fameAway";
  
  private int[] fRollHome;
  private int fSpectatorsHome;
  private int fFameHome;
  private int[] fRollAway;
  private int fSpectatorsAway;
  private int fFameAway;
  
  public ReportSpectators() {
    super();
  }

  public ReportSpectators(int[] pRollHome, int pSupportersHome, int pFameHome, int[] pRollAway, int pSupportersAway, int pFameAway) {
    fRollHome = pRollHome;
    fSpectatorsHome = pSupportersHome;
    fFameHome = pFameHome;
    fRollAway = pRollAway;
    fSpectatorsAway = pSupportersAway;
    fFameAway = pFameAway;
  }
  
  public ReportId getId() {
    return ReportId.SPECTATORS;
  }
  
  public int[] getRollHome() {
    return fRollHome;
  }
  
  public int getSpectatorsHome() {
    return fSpectatorsHome;
  }
  
  public int getFameHome() {
    return fFameHome;
  }
  
  public int[] getRollAway() {
    return fRollAway;
  }
  
  public int getSpectatorsAway() {
    return fSpectatorsAway;
  }
  
  public int getFameAway() {
    return fFameAway;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportSpectators(getRollAway(), getSpectatorsAway(), getFameAway(), getRollHome(), getSpectatorsHome(), getFameHome());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL_HOME, getRollHome());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SPECTATORS_HOME, getSpectatorsHome());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_FAME_HOME, getFameHome());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL_AWAY, getRollAway());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SPECTATORS_AWAY, getSpectatorsAway());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_FAME_AWAY, getFameAway());
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
    pByteList.addByteArray(getRollHome());
    pByteList.addInt(getSpectatorsHome());
    pByteList.addByte((byte) getFameHome());
    pByteList.addByteArray(getRollAway());
    pByteList.addInt(getSpectatorsAway());
    pByteList.addByte((byte) getFameAway());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fRollHome = pByteArray.getByteArrayAsIntArray();
    fSpectatorsHome = pByteArray.getInt();
    fFameHome = pByteArray.getByte();
    fRollAway = pByteArray.getByteArrayAsIntArray();
    fSpectatorsAway = pByteArray.getInt();
    fFameAway = pByteArray.getByte();
    return byteArraySerializationVersion;
  }
    
}
