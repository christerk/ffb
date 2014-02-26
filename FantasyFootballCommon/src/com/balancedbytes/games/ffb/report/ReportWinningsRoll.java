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
public class ReportWinningsRoll implements IReport {
  
  private int fWinningsRollHome;
  private int fWinningsHome;
  private int fWinningsRollAway;
  private int fWinningsAway;
  
  public ReportWinningsRoll() {
    super();
  }

  public ReportWinningsRoll(int pRollHome, int pWinningsHome, int pRollAway, int pWinningsAway) {
    fWinningsRollHome = pRollHome;
    fWinningsHome = pWinningsHome;
    fWinningsRollAway = pRollAway;
    fWinningsAway = pWinningsAway;
  }
  
  public ReportId getId() {
    return ReportId.WINNINGS_ROLL;
  }

  public int getWinningsRollHome() {
    return fWinningsRollHome;
  }

  public int getWinningsHome() {
    return fWinningsHome;
  }

  public int getWinningsRollAway() {
    return fWinningsRollAway;
  }

  public int getWinningsAway() {
    return fWinningsAway;
  }

  // transformation
  
  public IReport transform() {
    return new ReportWinningsRoll(getWinningsRollAway(), getWinningsAway(), getWinningsRollHome(), getWinningsHome());
  }
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fWinningsRollHome = pByteArray.getByte();
    fWinningsHome = pByteArray.getInt();
    fWinningsRollAway = pByteArray.getByte();
    fWinningsAway = pByteArray.getInt();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.WINNINGS_ROLL_HOME.addTo(jsonObject, fWinningsRollHome);
    IJsonOption.WINNINGS_HOME.addTo(jsonObject, fWinningsHome);
    IJsonOption.WINNINGS_ROLL_AWAY.addTo(jsonObject, fWinningsRollAway);
    IJsonOption.WINNINGS_AWAY.addTo(jsonObject, fWinningsAway);
    return jsonObject;
  }
  
  public ReportWinningsRoll initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fWinningsRollHome = IJsonOption.WINNINGS_ROLL_HOME.getFrom(jsonObject);
    fWinningsHome = IJsonOption.WINNINGS_HOME.getFrom(jsonObject);
    fWinningsRollAway = IJsonOption.WINNINGS_ROLL_AWAY.getFrom(jsonObject);
    fWinningsAway = IJsonOption.WINNINGS_AWAY.getFrom(jsonObject);
    return this;
  }
    
}
