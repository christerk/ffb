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
public class ReportCardsBought implements IReport {
  
  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_ATTRIBUTE_CARDS = "cards";
  private static final String _XML_ATTRIBUTE_GOLD = "gold";

  private String fTeamId;
  private int fNrOfCards;
  private int fGold;
  
  public ReportCardsBought() {
    super();
  }

  public ReportCardsBought(String pTeamId, int pNrOfCards, int pGold) {
  	fTeamId = pTeamId;
  	fNrOfCards = pNrOfCards;
  	fGold = pGold;
  }
  
  public ReportId getId() {
    return ReportId.CARDS_BOUGHT;
  }
  
  public String getTeamId() {
	  return fTeamId;
  }
  
  public int getNrOfCards() {
	  return fNrOfCards;
  }
  
  public int getGold() {
	  return fGold;
  }

  // transformation
  
  public IReport transform() {
    return new ReportCardsBought(getTeamId(), getNrOfCards(), getGold());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CARDS, getNrOfCards());
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
    pByteList.addByte((byte) getNrOfCards());
    pByteList.addInt(getGold());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTeamId = pByteArray.getString();
    fNrOfCards = pByteArray.getByte();
    fGold = pByteArray.getInt();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    IJsonOption.NR_OF_CARDS.addTo(jsonObject, fNrOfCards);
    IJsonOption.GOLD.addTo(jsonObject, fGold);
    return jsonObject;
  }
  
  public ReportCardsBought initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    fNrOfCards = IJsonOption.NR_OF_CARDS.getFrom(jsonObject);
    fGold = IJsonOption.GOLD.getFrom(jsonObject);
    return this;
  }
    
}
