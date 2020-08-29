package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class ReportRiotousRookies implements IReport {

  private int[] roll;
  private int amount;
  private boolean homeTeam;

  public ReportRiotousRookies() {
  }

  public ReportRiotousRookies(int[] roll, int amount, boolean homeTeam) {
    this.roll = roll;
    this.amount = amount;
    this.homeTeam = homeTeam;
  }

  @Override
  public ReportId getId() {
    return ReportId.RIOTOUS_ROOKIES;
  }

  @Override
  public IReport transform() {
    return new ReportRiotousRookies(roll, amount, homeTeam);
  }

  public int[] getRoll() {
    return roll;
  }

  public int getAmount() {
    return amount;
  }

  public boolean isHomeTeam() {
    return homeTeam;
  }

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.RIOTOUS_ROLL.addTo(jsonObject, roll);
    IJsonOption.RIOTOUS_AMOUNT.addTo(jsonObject, amount);
    IJsonOption.HOME_TEAM.addTo(jsonObject, homeTeam);
    return jsonObject;
  }

  public ReportRiotousRookies initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    roll = IJsonOption.RIOTOUS_ROLL.getFrom(jsonObject);
    amount = IJsonOption.RIOTOUS_AMOUNT.getFrom(jsonObject);
    homeTeam = IJsonOption.HOME_TEAM.getFrom(jsonObject);
    return this;
  }
}
