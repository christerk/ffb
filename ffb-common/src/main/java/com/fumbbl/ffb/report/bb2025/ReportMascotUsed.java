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
public class ReportMascotUsed extends NoDiceReport {

	private String teamId;
	private int roll, minimumRoll;
	private boolean successful, fallback;

	@SuppressWarnings("unused")
	public ReportMascotUsed() {
	}

	public ReportMascotUsed(String teamId, int minimumRoll, int roll, boolean successful, boolean fallback) {
		this.teamId = teamId;
		this.roll = roll;
		this.minimumRoll = minimumRoll;
		this.successful = successful;
		this.fallback = fallback;
	}

	public ReportId getId() {
		return ReportId.MASCOT_USED;
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

	public boolean isFallback() {
		return fallback;
	}
	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportMascotUsed(teamId, minimumRoll, roll, successful, fallback);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.MINIMUM_ROLL.addTo(jsonObject, minimumRoll);
		IJsonOption.ROLL.addTo(jsonObject, roll);
		IJsonOption.SUCCESSFUL.addTo(jsonObject, successful);
		IJsonOption.RE_ROLL_USED.addTo(jsonObject, fallback);
		return jsonObject;
	}

	public ReportMascotUsed initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		minimumRoll = IJsonOption.MINIMUM_ROLL.getFrom(source, jsonObject);
		roll = IJsonOption.ROLL.getFrom(source, jsonObject);
		successful = IJsonOption.SUCCESSFUL.getFrom(source, jsonObject);
		fallback = IJsonOption.RE_ROLL_USED.getFrom(source, jsonObject);
		return this;
	}

}
