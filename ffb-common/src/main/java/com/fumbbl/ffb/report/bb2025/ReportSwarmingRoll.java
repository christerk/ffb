package com.fumbbl.ffb.report.bb2025;

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

@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportSwarmingRoll implements IReport {

	private String teamId;
	private int roll;

	@SuppressWarnings("unused")
	public ReportSwarmingRoll() {
	}

	public ReportSwarmingRoll(String teamId, int roll) {
		this.teamId = teamId;
		this.roll = roll;
	}

	@Override
	public ReportId getId() {
		return ReportId.SWARMING_PLAYERS_ROLL;
	}

	public String getTeamId() {
		return teamId;
	}

	public int getRoll() {
		return roll;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportSwarmingRoll(teamId, roll);
	}

	@Override
	public Object initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		if (IJsonOption.SWARMING_PLAYER_ROLL.isDefinedIn(jsonObject)) {
			roll = IJsonOption.SWARMING_PLAYER_ROLL.getFrom(source, jsonObject);
		}
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.SWARMING_PLAYER_ROLL.addTo(jsonObject, roll);
		return jsonObject;
	}

	@Override
	public void addStats(Game game, List<DieStat<?>> diceStats) {
		diceStats.add(new DicePoolStat(DieBase.D3, TeamMapping.TEAM, teamId, Collections.singletonList(roll), false));
	}
}
