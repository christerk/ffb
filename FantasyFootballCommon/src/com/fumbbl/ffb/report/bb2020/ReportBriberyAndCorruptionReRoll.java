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
public class ReportBriberyAndCorruptionReRoll implements IReport {

	private String teamId;
	private boolean used;

	public ReportBriberyAndCorruptionReRoll() {
		super();
	}

	public ReportBriberyAndCorruptionReRoll(String teamId, boolean used) {
		this.teamId = teamId;
		this.used = used;
	}

	public ReportId getId() {
		return ReportId.BRIBERY_AND_CORRUPTION_RE_ROLL;
	}

	public boolean isUsed() {
		return used;
	}

	public String getTeamId() {
		return teamId;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportBriberyAndCorruptionReRoll(teamId, used);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.USED.addTo(jsonObject, used);
		return jsonObject;
	}

	public ReportBriberyAndCorruptionReRoll initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		used = IJsonOption.USED.getFrom(game, jsonObject);
		teamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		return this;
	}

}
