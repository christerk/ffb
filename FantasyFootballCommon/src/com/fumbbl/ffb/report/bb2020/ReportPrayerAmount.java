package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportPrayerAmount implements IReport {

	private int tvHome, tvAway, prayerAmount;
	private boolean homeTeamReceivesPrayers;

	public ReportPrayerAmount() {
		super();
	}

	public ReportPrayerAmount(int tvHome, int tvAway, int prayerAmount, boolean homeTeamReceivesPrayers) {
		this.tvHome = tvHome;
		this.tvAway = tvAway;
		this.prayerAmount = prayerAmount;
		this.homeTeamReceivesPrayers = homeTeamReceivesPrayers;
	}

	public ReportId getId() {
		return ReportId.PRAYER_AMOUNT;
	}

	public int getTvHome() {
		return tvHome;
	}

	public int getTvAway() {
		return tvAway;
	}

	public int getPrayerAmount() {
		return prayerAmount;
	}

	public boolean isHomeTeamReceivesPrayers() {
		return homeTeamReceivesPrayers;
	}

// transformation

	public IReport transform(IFactorySource source) {
		return new ReportPrayerAmount(tvAway, tvHome, prayerAmount, !homeTeamReceivesPrayers);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_VALUE.addTo(jsonObject, tvHome);
		IJsonOption.OPPONENT_TEAM_VALUE.addTo(jsonObject, tvAway);
		IJsonOption.HOME_TEAM.addTo(jsonObject, homeTeamReceivesPrayers);
		IJsonOption.NUMBER.addTo(jsonObject, prayerAmount);
		return jsonObject;
	}

	public ReportPrayerAmount initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		tvHome = IJsonOption.TEAM_VALUE.getFrom(game, jsonObject);
		tvAway = IJsonOption.OPPONENT_TEAM_VALUE.getFrom(game, jsonObject);
		homeTeamReceivesPrayers = IJsonOption.HOME_TEAM.getFrom(game, jsonObject);
		prayerAmount = IJsonOption.NUMBER.getFrom(game, jsonObject);
		return this;
	}

}
