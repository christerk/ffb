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
public class ReportFanFactorRoll implements IReport {
  
  private static final String _XML_ATTRIBUTE_ROLL_HOME = "rollHome";
  private static final String _XML_ATTRIBUTE_MODIFIER_HOME = "modifierHome";
  private static final String _XML_ATTRIBUTE_ROLL_AWAY = "rollAway";
  private static final String _XML_ATTRIBUTE_MODIFIER_AWAY = "modifierAway";

  private int[] fFanFactorRollHome;
  private int fFanFactorModifierHome;
  private int[] fFanFactorRollAway;
  private int fFanFactorModifierAway;
  
  public ReportFanFactorRoll() {
    super();
  }

  public ReportFanFactorRoll(int[] pFanFactorRollHome, int pFanFactorModifierHome, int[] pFanFactorRollAway, int pFanFactorModifierAway) {
    fFanFactorRollHome = pFanFactorRollHome;
    fFanFactorModifierHome = pFanFactorModifierHome;
    fFanFactorRollAway = pFanFactorRollAway;
    fFanFactorModifierAway = pFanFactorModifierAway;
  }
  
  public ReportId getId() {
    return ReportId.FAN_FACTOR_ROLL;
  }

  public int[] getFanFactorRollHome() {
    return fFanFactorRollHome;
  }

  public int getFanFactorModifierHome() {
    return fFanFactorModifierHome;
  }

  public int[] getFanFactorRollAway() {
    return fFanFactorRollAway;
  }

  public int getFanFactorModifierAway() {
    return fFanFactorModifierAway;
  }

  // transformation
  
  public IReport transform() {
    return new ReportFanFactorRoll(getFanFactorRollAway(), getFanFactorModifierAway(), getFanFactorRollHome(), getFanFactorModifierHome());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL_HOME, getFanFactorRollHome());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MODIFIER_HOME, getFanFactorModifierHome());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL_AWAY, getFanFactorRollAway());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MODIFIER_AWAY, getFanFactorModifierAway());
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
    pByteList.addByteArray(getFanFactorRollHome());
    pByteList.addByte((byte) getFanFactorModifierHome());
    pByteList.addByteArray(getFanFactorRollAway());
    pByteList.addByte((byte) getFanFactorModifierAway());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fFanFactorRollHome = pByteArray.getByteArrayAsIntArray();
    fFanFactorModifierHome = pByteArray.getByte();
    fFanFactorRollAway = pByteArray.getByteArrayAsIntArray();
    fFanFactorModifierAway = pByteArray.getByte();
    return byteArraySerializationVersion;
  }
 
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.FAN_FACTOR_ROLL_HOME.addTo(jsonObject, fFanFactorRollHome);
    IJsonOption.FAN_FACTOR_MODIFIER_HOME.addTo(jsonObject, fFanFactorModifierHome);
    IJsonOption.FAN_FACTOR_ROLL_AWAY.addTo(jsonObject, fFanFactorRollAway);
    IJsonOption.FAN_FACTOR_MODIFIER_AWAY.addTo(jsonObject, fFanFactorModifierAway);
    return jsonObject;
  }
  
  public ReportFanFactorRoll initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fFanFactorRollHome = IJsonOption.FAN_FACTOR_ROLL_HOME.getFrom(jsonObject);
    fFanFactorModifierHome = IJsonOption.FAN_FACTOR_MODIFIER_HOME.getFrom(jsonObject);
    fFanFactorRollAway = IJsonOption.FAN_FACTOR_ROLL_AWAY.getFrom(jsonObject);
    fFanFactorModifierAway = IJsonOption.FAN_FACTOR_MODIFIER_AWAY.getFrom(jsonObject);
    return this;
  }
        
}
