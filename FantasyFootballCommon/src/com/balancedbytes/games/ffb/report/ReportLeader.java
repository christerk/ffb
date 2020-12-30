package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.LeaderState;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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

	public ReportLeader initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		fLeaderState = (LeaderState) IJsonOption.LEADER_STATE.getFrom(game, jsonObject);
		return this;
	}

}
