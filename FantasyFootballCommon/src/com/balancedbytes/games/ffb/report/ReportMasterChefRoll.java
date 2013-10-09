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
public class ReportMasterChefRoll implements IReport {

  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_ATTRIBUTE_ROLL = "roll";
  private static final String _XML_ATTRIBUTE_RE_ROLLS_STOLEN = "reRollsStolen";

  private String fTeamId;
  private int[] fMasterChefRoll;
  private int fReRollsStolen;

  public ReportMasterChefRoll() {
    super();
  }

  public ReportMasterChefRoll(String pTeamId, int[] pRoll, int pReRollsStolen) {
    fTeamId = pTeamId;
    fMasterChefRoll = pRoll;
    fReRollsStolen = pReRollsStolen;
  }

  public ReportId getId() {
    return ReportId.MASTER_CHEF_ROLL;
  }

  public String getTeamId() {
    return fTeamId;
  }

  public int[] getMasterChefRoll() {
    return fMasterChefRoll;
  }

  public int getReRollsStolen() {
    return fReRollsStolen;
  }

  // transformation

  public IReport transform() {
    return new ReportMasterChefRoll(getTeamId(), getMasterChefRoll(), getReRollsStolen());
  }

  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL, getMasterChefRoll());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_RE_ROLLS_STOLEN, getReRollsStolen());
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
    pByteList.addByteArray(getMasterChefRoll());
    pByteList.addByte((byte) getReRollsStolen());
  }

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTeamId = pByteArray.getString();
    fMasterChefRoll = pByteArray.getByteArrayAsIntArray();
    fReRollsStolen = pByteArray.getByte();
    return byteArraySerializationVersion;
  }

  // JSON serialization

  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    IJsonOption.MASTER_CHEF_ROLL.addTo(jsonObject, fMasterChefRoll);
    IJsonOption.RE_ROLLS_STOLEN.addTo(jsonObject, fReRollsStolen);
    return jsonObject;
  }

  public ReportMasterChefRoll initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    fMasterChefRoll = IJsonOption.MASTER_CHEF_ROLL.getFrom(jsonObject);
    fReRollsStolen = IJsonOption.RE_ROLLS_STOLEN.getFrom(jsonObject);
    return this;
  }

}
