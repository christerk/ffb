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
public class ReportMasterChefRoll implements IReport {

  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_ATTRIBUTE_ROLL = "roll";
  private static final String _XML_ATTRIBUTE_RE_ROLLS_STOLEN = "reRollsStolen";

  private String fTeamId;
  private int[] fRoll;
  private int fReRollsStolen;

  public ReportMasterChefRoll() {
    super();
  }

  public ReportMasterChefRoll(String pTeamId, int[] pRoll, int pReRollsStolen) {
    fTeamId = pTeamId;
    fRoll = pRoll;
    fReRollsStolen = pReRollsStolen;
  }

  public ReportId getId() {
    return ReportId.MASTER_CHEF_ROLL;
  }
  
  public String getTeamId() {
    return fTeamId;
  }

  public int[] getRoll() {
    return fRoll;
  }

  public int getReRollsStolen() {
    return fReRollsStolen;
  }

  // transformation

  public IReport transform() {
    return new ReportMasterChefRoll(getTeamId(), getRoll(), getReRollsStolen());
  }

  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL, getRoll());
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
    pByteList.addByteArray(getRoll());
    pByteList.addByte((byte) getReRollsStolen());
  }

  public int initFrom(ByteArray pByteArray) {
    ReportId reportId = ReportId.fromId(pByteArray.getSmallInt());
    if (getId() != reportId) {
      throw new IllegalStateException("Wrong report id. Expected " + getId().getName() + " received " + ((reportId != null) ? reportId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTeamId = pByteArray.getString();
    fRoll = pByteArray.getByteArrayAsIntArray();
    fReRollsStolen = pByteArray.getByte();
    return byteArraySerializationVersion;
  }

}
