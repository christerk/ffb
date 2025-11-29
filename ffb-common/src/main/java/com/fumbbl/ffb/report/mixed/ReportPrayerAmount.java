package com.fumbbl.ffb.report.mixed;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.NoDiceReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportPrayerAmount extends NoDiceReport {

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

	public ReportPrayerAmount initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		tvHome = IJsonOption.TEAM_VALUE.getFrom(source, jsonObject);
		tvAway = IJsonOption.OPPONENT_TEAM_VALUE.getFrom(source, jsonObject);
		homeTeamReceivesPrayers = IJsonOption.HOME_TEAM.getFrom(source, jsonObject);
		prayerAmount = IJsonOption.NUMBER.getFrom(source, jsonObject);
		return this;
	}

}
