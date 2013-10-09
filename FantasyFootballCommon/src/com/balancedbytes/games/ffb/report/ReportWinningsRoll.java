package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ReportWinningsRoll implements IReport {
  
  private static final String _XML_ATTRIBUTE_ROLL_HOME = "rollHome";
  private static final String _XML_ATTRIBUTE_WINNINGS_HOME = "winningsHome";
  private static final String _XML_ATTRIBUTE_ROLL_AWAY = "rollAway";
  private static final String _XML_ATTRIBUTE_WINNINGS_AWAY = "winningsAway";

  private int fWinningsRollHome;
  private int fWinningsHome;
  private int fWinningsRollAway;
  private int fWinningsAway;
  
  public ReportWinningsRoll() {
    super();
  }

  public ReportWinningsRoll(int pRollHome, int pWinningsHome, int pRollAway, int pWinningsAway) {
    fWinningsRollHome = pRollHome;
    fWinningsHome = pWinningsHome;
    fWinningsRollAway = pRollAway;
    fWinningsAway = pWinningsAway;
  }
  
  public ReportId getId() {
    return ReportId.WINNINGS_ROLL;
  }

  public int getWinningsRollHome() {
    return fWinningsRollHome;
  }

  public int getWinningsHome() {
    return fWinningsHome;
  }

  public int getWinningsRollAway() {
    return fWinningsRollAway;
  }

  public int getWinningsAway() {
    return fWinningsAway;
  }

  // transformation
  
  public IReport transform() {
    return new ReportWinningsRoll(getWinningsRollAway(), getWinningsAway(), getWinningsRollHome(), getWinningsHome());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL_HOME, getWinningsRollHome());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_WINNINGS_HOME, getWinningsHome());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL_AWAY, getWinningsRollAway());
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
    pByteList.addByte((byte) getWinningsRollHome());
    pByteList.addInt(getWinningsHome());
    pByteList.addByte((byte) getWinningsRollAway());
    pByteList.addInt(getWinningsAway());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fWinningsRollHome = pByteArray.getByte();
    fWinningsHome = pByteArray.getInt();
    fWinningsRollAway = pByteArray.getByte();
    fWinningsAway = pByteArray.getInt();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.WINNINGS_ROLL_HOME.addTo(jsonObject, fWinningsRollHome);
    IJsonOption.WINNINGS_HOME.addTo(jsonObject, fWinningsHome);
    IJsonOption.WINNINGS_ROLL_AWAY.addTo(jsonObject, fWinningsRollAway);
    IJsonOption.WINNINGS_AWAY.addTo(jsonObject, fWinningsAway);
    return jsonObject;
  }
  
  public ReportWinningsRoll initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fWinningsRollHome = IJsonOption.WINNINGS_ROLL_HOME.getFrom(jsonObject);
    fWinningsHome = IJsonOption.WINNINGS_HOME.getFrom(jsonObject);
    fWinningsRollAway = IJsonOption.WINNINGS_ROLL_AWAY.getFrom(jsonObject);
    fWinningsAway = IJsonOption.WINNINGS_AWAY.getFrom(jsonObject);
    return this;
  }
    
}
