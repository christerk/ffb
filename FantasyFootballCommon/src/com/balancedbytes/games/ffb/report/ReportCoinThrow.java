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
public class ReportCoinThrow implements IReport {

  private boolean fCoinThrowHeads;
  private String fCoach;
  private boolean fCoinChoiceHeads;
  
  public ReportCoinThrow() {
    super();
  }
  
  public ReportCoinThrow(boolean pCoinThrowHeads, String pCoach, boolean pCoinChoiceHeads) {
    fCoinThrowHeads = pCoinThrowHeads;
    fCoach = pCoach;
    fCoinChoiceHeads = pCoinChoiceHeads;
  }
  
  public ReportId getId() {
    return ReportId.COIN_THROW;
  }
  
  public boolean isCoinThrowHeads() {
    return fCoinThrowHeads;
  }
  
  public String getCoach() {
    return fCoach;
  }
  
  public boolean isCoinChoiceHeads() {
    return fCoinChoiceHeads;
  }

  // transformation
  
  public IReport transform() {
    return new ReportCoinThrow(isCoinThrowHeads(), getCoach(), isCoinChoiceHeads());
  }
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fCoinThrowHeads = pByteArray.getBoolean();
    fCoach = pByteArray.getString();
    fCoinChoiceHeads = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
    
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.COACH.addTo(jsonObject, fCoach);
    IJsonOption.COIN_THROW_HEADS.addTo(jsonObject, fCoinThrowHeads);
    IJsonOption.COIN_CHOICE_HEADS.addTo(jsonObject, fCoinChoiceHeads);
    return jsonObject;
  }
  
  public ReportCoinThrow initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fCoach = IJsonOption.COACH.getFrom(jsonObject);
    fCoinThrowHeads = IJsonOption.COIN_THROW_HEADS.getFrom(jsonObject);
    fCoinChoiceHeads = IJsonOption.COIN_CHOICE_HEADS.getFrom(jsonObject);
    return this;
  }
 
}