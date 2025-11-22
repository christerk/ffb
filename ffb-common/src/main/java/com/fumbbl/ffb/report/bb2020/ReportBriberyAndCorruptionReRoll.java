package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.BriberyAndCorruptionAction;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.NoDiceReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportBriberyAndCorruptionReRoll extends NoDiceReport {

	private String teamId;
	private BriberyAndCorruptionAction action;

	@SuppressWarnings("unused")
	public ReportBriberyAndCorruptionReRoll() {
		super();
	}

	public ReportBriberyAndCorruptionReRoll(String teamId, BriberyAndCorruptionAction action) {
		this.teamId = teamId;
		this.action = action;
	}

	public ReportId getId() {
		return ReportId.BRIBERY_AND_CORRUPTION_RE_ROLL;
	}

	public BriberyAndCorruptionAction getAction() {
		return action;
	}

	public String getTeamId() {
		return teamId;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportBriberyAndCorruptionReRoll(teamId, action);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.BRIBERY_AND_CORRUPTION_ACTION.addTo(jsonObject, action.name());
		return jsonObject;
	}

	public ReportBriberyAndCorruptionReRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		action = BriberyAndCorruptionAction.valueOf(IJsonOption.BRIBERY_AND_CORRUPTION_ACTION.getFrom(source, jsonObject));
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		return this;
	}

}
