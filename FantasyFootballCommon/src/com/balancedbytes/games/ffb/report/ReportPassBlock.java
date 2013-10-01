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
public class ReportPassBlock implements IReport {

  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_ATTRIBUTE_PASS_BLOCK_AVAILABLE = "passBlockAvailable";

  private String fTeamId;
  private boolean fPassBlockAvailable;

  public ReportPassBlock() {
    super();
  }

  public ReportPassBlock(String pTeamId, boolean pPassBlockAvailable) {
    fTeamId = pTeamId;
    fPassBlockAvailable = pPassBlockAvailable;
  }

  public ReportId getId() {
    return ReportId.PASS_BLOCK;
  }

  public String getTeamId() {
    return fTeamId;
  }

  public boolean isPassBlockAvailable() {
    return fPassBlockAvailable;
  }

  // transformation

  public IReport transform() {
    return new ReportPassBlock(getTeamId(), isPassBlockAvailable());
  }

  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PASS_BLOCK_AVAILABLE, isPassBlockAvailable());
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
    pByteList.addBoolean(isPassBlockAvailable());
  }

  public int initFrom(ByteArray pByteArray) {
    ReportId reportId = ReportId.fromId(pByteArray.getSmallInt());
    if (getId() != reportId) {
      throw new IllegalStateException("Wrong report id. Expected " + getId().getName() + " received " + ((reportId != null) ? reportId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTeamId = pByteArray.getString();
    fPassBlockAvailable = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }

}
