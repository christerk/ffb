package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportSwarmingRoll implements IReport {

	private String teamId;
	private int amount, roll = -1, limit = -1;

	public ReportSwarmingRoll() {
	}

	public ReportSwarmingRoll(String teamId, int amount, int roll, int limit) {
		this.teamId = teamId;
		this.amount = amount;
		this.roll = roll;
		this.limit = limit;
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

	public int getRoll() {
		return roll;
	}

	public int getLimit() {
		return limit;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportSwarmingRoll(teamId, amount, roll, limit);
	}

	@Override
	public Object initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		amount = IJsonOption.SWARMING_PLAYER_AMOUNT.getFrom(game, jsonObject);
		teamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		if (IJsonOption.SWARMING_PLAYER_LIMIT.isDefinedIn(jsonObject)) {
			limit = IJsonOption.SWARMING_PLAYER_LIMIT.getFrom(game, jsonObject);
		}

		if (IJsonOption.SWARMING_PLAYER_ROLL.isDefinedIn(jsonObject)) {
			roll = IJsonOption.SWARMING_PLAYER_ROLL.getFrom(game, jsonObject);
		}
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.SWARMING_PLAYER_AMOUNT.addTo(jsonObject, amount);
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.SWARMING_PLAYER_ROLL.addTo(jsonObject, roll);
		IJsonOption.SWARMING_PLAYER_LIMIT.addTo(jsonObject, limit);
		return jsonObject;
	}
}
