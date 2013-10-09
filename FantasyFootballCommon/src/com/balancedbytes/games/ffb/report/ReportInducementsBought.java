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
public class ReportInducementsBought implements IReport {
  
	private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_ATTRIBUTE_INDUCEMENTS = "inducements";
  private static final String _XML_ATTRIBUTE_STARS = "stars";
  private static final String _XML_ATTRIBUTE_MERCENARIES = "mercenaries";
  private static final String _XML_ATTRIBUTE_GOLD = "gold";

  private String fTeamId;
  private int fNrOfInducements;
  private int fNrOfStars;
  private int fNrOfMercenaries;
  private int fGold;
  
  public ReportInducementsBought() {
    super();
  }

  public ReportInducementsBought(String pTeamId, int pInducements, int pStars, int pMercenaries, int pGold) {
  	fTeamId = pTeamId;
  	fNrOfInducements = pInducements;
  	fNrOfStars = pStars;
  	fNrOfMercenaries = pMercenaries;
  	fGold = pGold;
  }
  
  public ReportId getId() {
    return ReportId.INDUCEMENTS_BOUGHT;
  }
  
  public String getTeamId() {
	  return fTeamId;
  }
  
  public int getNrOfInducements() {
	  return fNrOfInducements;
  }
  
  public int getNrOfStars() {
	  return fNrOfStars;
  }
  
  public int getNrOfMercenaries() {
	  return fNrOfMercenaries;
  }
  
  public int getGold() {
	  return fGold;
  }

  // transformation
  
  public IReport transform() {
    return new ReportInducementsBought(getTeamId(), getNrOfInducements(), getNrOfStars(), getNrOfMercenaries(), getGold());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_INDUCEMENTS, getNrOfInducements());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_STARS, getNrOfStars());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MERCENARIES, getNrOfMercenaries());
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
    pByteList.addByte((byte) getNrOfInducements());
    pByteList.addByte((byte) getNrOfStars());
    pByteList.addByte((byte) getNrOfMercenaries());
    pByteList.addInt(getGold());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTeamId = pByteArray.getString();
    fNrOfInducements = pByteArray.getByte();
    fNrOfStars = pByteArray.getByte();
    fNrOfMercenaries = pByteArray.getByte();
    fGold = pByteArray.getInt();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    IJsonOption.NR_OF_INDUCEMENTS.addTo(jsonObject, fNrOfInducements);
    IJsonOption.NR_OF_STARS.addTo(jsonObject, fNrOfStars);
    IJsonOption.NR_OF_MERCENARIES.addTo(jsonObject, fNrOfMercenaries);
    IJsonOption.GOLD.addTo(jsonObject, fGold);
    return jsonObject;
  }
  
  public ReportInducementsBought initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    fNrOfInducements = IJsonOption.NR_OF_INDUCEMENTS.getFrom(jsonObject);
    fNrOfStars = IJsonOption.NR_OF_STARS.getFrom(jsonObject);
    fNrOfMercenaries = IJsonOption.NR_OF_MERCENARIES.getFrom(jsonObject);
    fGold = IJsonOption.GOLD.getFrom(jsonObject);
    return this;
  }   
    
}
