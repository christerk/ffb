package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ReportPettyCash implements IReport {
  
  private String fTeamId;
  private int fGold;
  
  public ReportPettyCash() {
    super();
  }

  public ReportPettyCash(String pTeamId, int pGold) {
  	fTeamId = pTeamId;
  	fGold = pGold;
  }
  
  public ReportId getId() {
    return ReportId.PETTY_CASH;
  }
  
  public String getTeamId() {
	  return fTeamId;
  }
  
  public int getGold() {
	  return fGold;
  }

  // transformation
  
  public IReport transform() {
    return new ReportPettyCash(getTeamId(), getGold());
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getTeamId());
    pByteList.addInt(getGold());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTeamId = pByteArray.getString();
    fGold = pByteArray.getInt();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    IJsonOption.GOLD.addTo(jsonObject, fGold);
    return jsonObject;
  }
  
  public ReportPettyCash initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    fGold = IJsonOption.GOLD.getFrom(jsonObject);
    return this;
  }    
    
}
