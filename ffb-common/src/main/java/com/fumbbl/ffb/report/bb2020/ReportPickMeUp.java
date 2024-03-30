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

import java.util.Collections;
import java.util.List;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportPickMeUp implements IReport {

	private String playerId;
	private boolean success;
	private int roll;

	public ReportPickMeUp() {
	}

	public ReportPickMeUp(String playerId, int roll, boolean success) {
		this.playerId = playerId;
		this.success = success;
		this.roll = roll;
	}

	public String getPlayerId() {
		return playerId;
	}

	public boolean isSuccess() {
		return success;
	}

	public int getRoll() {
		return roll;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.SUCCESSFUL.addTo(jsonObject, success);
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.ROLL.addTo(jsonObject, roll);
		return jsonObject;
	}

	public ReportPickMeUp initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		success = IJsonOption.SUCCESSFUL.getFrom(game, jsonObject);
		playerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		roll = IJsonOption.ROLL.getFrom(game, jsonObject);
		return this;
	}


	@Override
	public ReportId getId() {
		return ReportId.PICK_ME_UP;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportPickMeUp(playerId, roll, success);
	}

	@Override
	public void addStats(Game game, List<DieStat<?>> diceStats) {
		diceStats.add(new DicePoolStat(DieBase.D6, TeamMapping.TEAM_FOR_PLAYER, playerId, Collections.singletonList(roll)));
	}
}
