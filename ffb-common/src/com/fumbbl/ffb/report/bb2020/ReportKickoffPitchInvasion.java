package com.fumbbl.ffb.report.bb2020;

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
import com.fumbbl.ffb.util.ArrayTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportKickoffPitchInvasion implements IReport {

	private int rollHome, rollAway, amount;
	private final List<String> affectedPlayers = new ArrayList<>();

	public ReportKickoffPitchInvasion() {
	}

	public ReportKickoffPitchInvasion(int rollHome, int rollAway, List<String> affectedPlayers, int amount) {
		this.rollHome = rollHome;
		this.rollAway = rollAway;
		this.amount = amount;
		this.affectedPlayers.addAll(affectedPlayers);
	}

	public int getRollHome() {
		return rollHome;
	}

	public int getRollAway() {
		return rollAway;
	}

	public List<String> getAffectedPlayers() {
		return affectedPlayers;
	}

	public int getAmount() {
		return amount;
	}

	public ReportId getId() {
		return ReportId.KICKOFF_PITCH_INVASION;
	}

	// transformation

	public ReportKickoffPitchInvasion transform(IFactorySource source) {
		return new ReportKickoffPitchInvasion(rollAway, rollHome, affectedPlayers, amount);
	}

	@Override
	public void addStats(Game game, List<DieStat<?>> diceStats) {
		diceStats.add(new DicePoolStat(DieBase.D6, TeamMapping.TEAM, game.getTeamHome().getId(), Collections.singletonList(rollHome)));
		diceStats.add(new DicePoolStat(DieBase.D6, TeamMapping.TEAM, game.getTeamAway().getId(), Collections.singletonList(rollAway)));
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.ROLL_HOME.addTo(jsonObject, rollHome);
		IJsonOption.ROLL_AWAY.addTo(jsonObject, rollAway);
		IJsonOption.AMOUNT.addTo(jsonObject, amount);
		IJsonOption.PLAYER_IDS.addTo(jsonObject, affectedPlayers);
		return jsonObject;
	}

	public ReportKickoffPitchInvasion initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		rollHome = IJsonOption.ROLL_HOME.getFrom(source, jsonObject);
		rollAway = IJsonOption.ROLL_AWAY.getFrom(source, jsonObject);
		amount = IJsonOption.AMOUNT.getFrom(source, jsonObject);
		affectedPlayers.clear();
		String[] affected = IJsonOption.PLAYER_IDS.getFrom(source, jsonObject);
		if (ArrayTool.isProvided(affected)) {
			affectedPlayers.addAll(Arrays.stream(affected).collect(Collectors.toList()));
		}
		return this;
	}

}
