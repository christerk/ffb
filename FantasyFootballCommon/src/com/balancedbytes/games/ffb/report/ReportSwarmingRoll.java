package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class ReportSwarmingRoll implements IReport {

	private String teamId;
	private int amount;

	public ReportSwarmingRoll() {
	}

	public ReportSwarmingRoll(String teamId, int amount) {
		this.teamId = teamId;
		this.amount = amount;
	}

	@Override
	public ReportId getId() {
		return ReportId.SWARMING_PLAYERS_ROLL;
	}

	public String getTeamId() {
		return teamId;
	}

	public int getAmount() {
		return amount;
	}

	@Override
	public IReport transform(Game game) {
		return new ReportSwarmingRoll(teamId, amount);
	}

	@Override
	public Object initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		amount = IJsonOption.SWARMING_PLAYER_AMOUNT.getFrom(game, jsonObject);
		teamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.SWARMING_PLAYER_AMOUNT.addTo(jsonObject, amount);
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		return jsonObject;
	}
}
