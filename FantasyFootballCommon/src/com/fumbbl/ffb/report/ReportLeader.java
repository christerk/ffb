package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.LeaderState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportLeader implements IReport {

	private String fTeamId;
	private LeaderState fLeaderState;

	public ReportLeader() {
		super();
	}

	public ReportLeader(String pTeamId, LeaderState pLeaderState) {
		fTeamId = pTeamId;
		fLeaderState = pLeaderState;
	}

	public ReportId getId() {
		return ReportId.LEADER;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public LeaderState getLeaderState() {
		return fLeaderState;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportLeader(getTeamId(), getLeaderState());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		IJsonOption.LEADER_STATE.addTo(jsonObject, fLeaderState);
		return jsonObject;
	}

	public ReportLeader initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		fLeaderState = (LeaderState) IJsonOption.LEADER_STATE.getFrom(source, jsonObject);
		return this;
	}

}
