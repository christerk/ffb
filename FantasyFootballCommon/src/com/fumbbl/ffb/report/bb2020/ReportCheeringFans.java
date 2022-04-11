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
public class ReportCheeringFans implements IReport {

	int rollHome, rollAway;
	private String teamId;
	private boolean prayerAvailable;

	public ReportCheeringFans() {
		super();
	}

	public ReportCheeringFans(String teamId, boolean prayerAvailable, int rollHome, int rollAway) {
		this.teamId = teamId;
		this.prayerAvailable = prayerAvailable;
		this.rollHome = rollHome;
		this.rollAway = rollAway;
	}

	public ReportId getId() {
		return ReportId.KICKOFF_CHEERING_FANS;
	}

	public boolean isPrayerAvailable() {
		return prayerAvailable;
	}

	public int getRollHome() {
		return rollHome;
	}

	public int getRollAway() {
		return rollAway;
	}

	public String getTeamId() {
		return teamId;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportCheeringFans(teamId, prayerAvailable, rollAway, rollHome);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.PRAYER_AVAILABLE.addTo(jsonObject, prayerAvailable);
		IJsonOption.ROLL_HOME.addTo(jsonObject, rollHome);
		IJsonOption.ROLL_AWAY.addTo(jsonObject, rollAway);
		return jsonObject;
	}

	public ReportCheeringFans initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		prayerAvailable = IJsonOption.PRAYER_AVAILABLE.getFrom(source, jsonObject);
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		rollAway = IJsonOption.ROLL_AWAY.getFrom(source, jsonObject);
		rollHome = IJsonOption.ROLL_HOME.getFrom(source, jsonObject);
		return this;
	}

}
