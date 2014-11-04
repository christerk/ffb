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
public class ReportInducementsBought implements IReport {
  
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
  
  // ByteArray serialization
  
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
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
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
