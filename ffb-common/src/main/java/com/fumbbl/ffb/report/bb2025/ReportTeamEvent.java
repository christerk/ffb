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
public class ReportTeamEvent extends NoDiceReport {

	private String teamId, eventMessage;

	@SuppressWarnings("unused")
	public ReportTeamEvent() {
	}

	public ReportTeamEvent(String teamId, String eventMessage) {
		this.teamId = teamId;
		this.eventMessage = eventMessage;
	}

	public ReportId getId() {
		return ReportId.TEAM_EVENT;
	}

	public String getTeamId() {
		return teamId;
	}

	public String getEventMessage() {
		return eventMessage;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportTeamEvent(teamId, eventMessage);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.MESSAGE.addTo(jsonObject, eventMessage);
		return jsonObject;
	}

	public ReportTeamEvent initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		eventMessage = IJsonOption.MESSAGE.getFrom(source, jsonObject);
		return this;
	}

}
