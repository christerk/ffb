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
public class ReportFanFactorRoll implements IReport {
  
  private static final String _XML_ATTRIBUTE_ROLL_HOME = "rollHome";
  private static final String _XML_ATTRIBUTE_MODIFIER_HOME = "modifierHome";
  private static final String _XML_ATTRIBUTE_ROLL_AWAY = "rollAway";
  private static final String _XML_ATTRIBUTE_MODIFIER_AWAY = "modifierAway";

  private int[] fRollHome;
  private int fModifierHome;
  private int[] fRollAway;
  private int fModifierAway;
  
  public ReportFanFactorRoll() {
    super();
  }

  public ReportFanFactorRoll(int[] pRollHome, int pModifierHome, int[] pRollAway, int pModifierAway) {
    fRollHome = pRollHome;
    fModifierHome = pModifierHome;
    fRollAway = pRollAway;
    fModifierAway = pModifierAway;
  }
  
  public ReportId getId() {
    return ReportId.FAN_FACTOR_ROLL;
  }

  public int[] getRollHome() {
    return fRollHome;
  }

  public int getModifierHome() {
    return fModifierHome;
  }

  public int[] getRollAway() {
    return fRollAway;
  }

  public int getModifierAway() {
    return fModifierAway;
  }

  // transformation
  
  public IReport transform() {
    return new ReportFanFactorRoll(getRollAway(), getModifierAway(), getRollHome(), getModifierHome());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL_HOME, getRollHome());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MODIFIER_HOME, getModifierHome());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL_AWAY, getRollAway());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MODIFIER_AWAY, getModifierAway());
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
    pByteList.addByte((byte) getModifierHome());
    pByteList.addByteArray(getRollAway());
    pByteList.addByte((byte) getModifierAway());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fRollHome = pByteArray.getByteArrayAsIntArray();
    fModifierHome = pByteArray.getByte();
    fRollAway = pByteArray.getByteArrayAsIntArray();
    fModifierAway = pByteArray.getByte();
    return byteArraySerializationVersion;
  }
    
}
