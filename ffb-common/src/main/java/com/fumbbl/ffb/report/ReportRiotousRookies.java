package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.stats.DicePoolStat;
import com.fumbbl.ffb.stats.DieBase;
import com.fumbbl.ffb.stats.DieStat;
import com.fumbbl.ffb.stats.TeamMapping;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportRiotousRookies implements IReport {

	private int[] roll;
	private int amount;
	private String teamId;

	public ReportRiotousRookies() {
	}

	public ReportRiotousRookies(int[] roll, int amount, String teamId) {
		this.roll = roll;
		this.amount = amount;
		this.teamId = teamId;
	}

	@Override
	public ReportId getId() {
		return ReportId.RIOTOUS_ROOKIES;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportRiotousRookies(roll, amount, teamId);
	}

	public int[] getRoll() {
		return roll;
	}

	public int getAmount() {
		return amount;
	}

	public String getTeamId() {
		return teamId;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.RIOTOUS_ROLL.addTo(jsonObject, roll);
		IJsonOption.RIOTOUS_AMOUNT.addTo(jsonObject, amount);
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		return jsonObject;
	}

	public ReportRiotousRookies initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		roll = IJsonOption.RIOTOUS_ROLL.getFrom(source, jsonObject);
		amount = IJsonOption.RIOTOUS_AMOUNT.getFrom(source, jsonObject);
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public void addStats(Game game, List<DieStat<?>> diceStats) {
		diceStats.add(new DicePoolStat(DieBase.D3, TeamMapping.TEAM, teamId, Arrays.stream(roll).boxed().collect(Collectors.toList()), false));
	}
}
