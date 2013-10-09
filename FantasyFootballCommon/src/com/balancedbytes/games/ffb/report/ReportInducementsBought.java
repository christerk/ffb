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
public class ReportInducementsBought implements IReport {
  
	private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_ATTRIBUTE_INDUCEMENTS = "inducements";
  private static final String _XML_ATTRIBUTE_STARS = "stars";
  private static final String _XML_ATTRIBUTE_MERCENARIES = "mercenaries";
  private static final String _XML_ATTRIBUTE_GOLD = "gold";

  private String fTeamId;
  private int fInducements;
  private int fStars;
  private int fMercenaries;
  private int fGold;
  
  public ReportInducementsBought() {
    super();
  }

  public ReportInducementsBought(String pTeamId, int pInducements, int pStars, int pMercenaries, int pGold) {
  	fTeamId = pTeamId;
  	fInducements = pInducements;
  	fStars = pStars;
  	fMercenaries = pMercenaries;
  	fGold = pGold;
  }
  
  public ReportId getId() {
    return ReportId.INDUCEMENTS_BOUGHT;
  }
  
  public String getTeamId() {
	  return fTeamId;
  }
  
  public int getInducements() {
	  return fInducements;
  }
  
  public int getStars() {
	  return fStars;
  }
  
  public int getMercenaries() {
	  return fMercenaries;
  }
  
  public int getGold() {
	  return fGold;
  }

  // transformation
  
  public IReport transform() {
    return new ReportInducementsBought(getTeamId(), getInducements(), getStars(), getMercenaries(), getGold());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_INDUCEMENTS, getInducements());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_STARS, getStars());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MERCENARIES, getMercenaries());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_GOLD, getGold());
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
    pByteList.addByte((byte) getInducements());
    pByteList.addByte((byte) getStars());
    pByteList.addByte((byte) getMercenaries());
    pByteList.addInt(getGold());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTeamId = pByteArray.getString();
    fInducements = pByteArray.getByte();
    fStars = pByteArray.getByte();
    fMercenaries = pByteArray.getByte();
    fGold = pByteArray.getInt();
    return byteArraySerializationVersion;
  }
    
}
