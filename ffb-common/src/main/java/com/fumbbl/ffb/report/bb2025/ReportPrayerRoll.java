package com.fumbbl.ffb.report.bb2025;

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

@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportPrayerRoll extends NoDiceReport {

	private int roll;
	private String teamName;
	private boolean homeTeam;

	public ReportPrayerRoll() {
		super();
	}

	public ReportPrayerRoll(String teamName, int roll, boolean homeTeam) {
		this.roll = roll;
		this.teamName = teamName;
		this.homeTeam = homeTeam;
	}

	public ReportId getId() {
		return ReportId.PRAYER_ROLL;
	}

	public int getRoll() {
		return roll;
	}

	public String getTeamName() {
		return teamName;
	}

	public boolean isHomeTeam() {
		return homeTeam;
	}
// transformation

	public IReport transform(IFactorySource source) {
		return new ReportPrayerRoll(teamName, roll, homeTeam);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.ROLL.addTo(jsonObject, roll);
		IJsonOption.TEAM_NAME.addTo(jsonObject, teamName);
		IJsonOption.HOME_TEAM.addTo(jsonObject, homeTeam);
		return jsonObject;
	}

	public ReportPrayerRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		roll = IJsonOption.ROLL.getFrom(source, jsonObject);
		teamName = IJsonOption.TEAM_NAME.getFrom(source, jsonObject);
		homeTeam = IJsonOption.HOME_TEAM.getFrom(source, jsonObject);
		return this;
	}

}
