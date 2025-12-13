package com.fumbbl.ffb.report.mixed;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;
import com.fumbbl.ffb.stats.DicePoolStat;
import com.fumbbl.ffb.stats.DieBase;
import com.fumbbl.ffb.stats.DieStat;
import com.fumbbl.ffb.stats.TeamMapping;

import java.util.Collections;
import java.util.List;

@RulesCollection(RulesCollection.Rules.BB2016)
@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportSwarmingRoll implements IReport {

	private String teamId;
	private int amount, roll = -1, limit = -1;

	@SuppressWarnings("unused")
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
	public Object initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		amount = IJsonOption.SWARMING_PLAYER_AMOUNT.getFrom(source, jsonObject);
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		if (IJsonOption.SWARMING_PLAYER_LIMIT.isDefinedIn(jsonObject)) {
			limit = IJsonOption.SWARMING_PLAYER_LIMIT.getFrom(source, jsonObject);
		}

		if (IJsonOption.SWARMING_PLAYER_ROLL.isDefinedIn(jsonObject)) {
			roll = IJsonOption.SWARMING_PLAYER_ROLL.getFrom(source, jsonObject);
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

	@Override
	public void addStats(Game game, List<DieStat<?>> diceStats) {
		diceStats.add(new DicePoolStat(DieBase.D3, TeamMapping.TEAM, teamId, Collections.singletonList(roll), false));
	}
}
