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

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportQuickSnapRoll implements IReport {

	private String teamId;
	private int amount;
	private int roll;

	public ReportQuickSnapRoll() {
	}

	public ReportQuickSnapRoll(String teamId, int roll, int amount) {
		this.teamId = teamId;
		this.amount = amount;
		this.roll = roll;
	}

	@Override
	public ReportId getId() {
		return ReportId.QUICK_SNAP_ROLL;
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

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportQuickSnapRoll(teamId, roll, amount);
	}

	@Override
	public void addStats(Game game, List<DieStat<?>> diceStats) {
		diceStats.add(new DicePoolStat(DieBase.D3, TeamMapping.TEAM, teamId, Collections.singletonList(roll)));
	}

	@Override
	public Object initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		amount = IJsonOption.NR_OF_PLAYERS.getFrom(source, jsonObject);
		roll = IJsonOption.ROLL.getFrom(source, jsonObject);
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.NR_OF_PLAYERS.addTo(jsonObject, amount);
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.ROLL.addTo(jsonObject, roll);
		return jsonObject;
	}
}
