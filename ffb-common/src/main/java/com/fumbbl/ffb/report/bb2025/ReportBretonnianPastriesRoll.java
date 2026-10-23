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
import com.fumbbl.ffb.stats.DieBase;
import com.fumbbl.ffb.stats.DieStat;
import com.fumbbl.ffb.stats.SingleDieStat;
import com.fumbbl.ffb.stats.TeamMapping;

import java.util.List;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportBretonnianPastriesRoll implements IReport {

	public static final int MINIMUM_ROLL = 2;

	private String teamId;
	private String playerId;
	private int roll;
	private boolean successful;

	public ReportBretonnianPastriesRoll() {
	}

	public ReportBretonnianPastriesRoll(String teamId, String playerId, int roll, boolean successful) {
		this.teamId = teamId;
		this.playerId = playerId;
		this.roll = roll;
		this.successful = successful;
	}

	@Override
	public ReportId getId() {
		return ReportId.BRETONNIAN_PASTRIES_ROLL;
	}

	public String getTeamId() {
		return teamId;
	}

	public String getPlayerId() {
		return playerId;
	}

	public int getRoll() {
		return roll;
	}

	public boolean isSuccessful() {
		return successful;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportBretonnianPastriesRoll(teamId, playerId, roll, successful);
	}

	@Override
	public void addStats(Game game, List<DieStat<?>> diceStats) {
		diceStats.add(new SingleDieStat(DieBase.D6, TeamMapping.TEAM_FOR_PLAYER, playerId, roll, MINIMUM_ROLL, getId(),
			successful));
	}

	@Override
	public Object initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		roll = IJsonOption.ROLL.getFrom(source, jsonObject);
		successful = IJsonOption.SUCCESSFUL.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.ROLL.addTo(jsonObject, roll);
		IJsonOption.SUCCESSFUL.addTo(jsonObject, successful);
		return jsonObject;
	}
}
