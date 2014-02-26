package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ReportCardsBought implements IReport {
  
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
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTeamId = pByteArray.getString();
    fNrOfCards = pByteArray.getByte();
    fGold = pByteArray.getInt();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
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
