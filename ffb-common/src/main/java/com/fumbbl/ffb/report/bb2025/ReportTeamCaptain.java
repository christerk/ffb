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
public class ReportTeamCaptain extends NoDiceReport {

	private String teamId;
	private int roll, minimumRoll;
	private boolean successful;

	@SuppressWarnings("unused")
	public ReportTeamCaptain() {
	}

	public ReportTeamCaptain(String teamId, int minimumRoll, int roll, boolean successful) {
		this.teamId = teamId;
		this.roll = roll;
		this.minimumRoll = minimumRoll;
		this.successful = successful;
	}

	public ReportId getId() {
		return ReportId.TEAM_CAPTAIN;
	}

	public String getTeamId() {
		return teamId;
	}

	public int getRoll() {
		return roll;
	}

	public int getMinimumRoll() {
		return minimumRoll;
	}

	public boolean isSuccessful() {
		return successful;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportTeamCaptain(teamId, minimumRoll, roll, successful);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.MINIMUM_ROLL.addTo(jsonObject, minimumRoll);
		IJsonOption.ROLL.addTo(jsonObject, roll);
		IJsonOption.SUCCESSFUL.addTo(jsonObject, successful);
		return jsonObject;
	}

	public ReportTeamCaptain initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		minimumRoll = IJsonOption.MINIMUM_ROLL.getFrom(source, jsonObject);
		roll = IJsonOption.ROLL.getFrom(source, jsonObject);
		successful = IJsonOption.SUCCESSFUL.getFrom(source, jsonObject);
		return this;
	}

}
