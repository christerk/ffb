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
public class ReportThrowAtPlayer implements IReport {

	private String fPlayerId;
	private int roll;
	private boolean successful;

	@SuppressWarnings("unused")
	public ReportThrowAtPlayer() {
	}

	public ReportThrowAtPlayer(String fPlayerId, int roll, boolean successful) {
		this.fPlayerId = fPlayerId;
		this.roll = roll;
		this.successful = successful;
	}

	public ReportId getId() {
		return ReportId.THROW_AT_PLAYER;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public int getRoll() {
		return roll;
	}

	public boolean isSuccessful() {
		return successful;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportThrowAtPlayer(getPlayerId(), roll, successful);
	}

	@Override
	public void addStats(Game game, List<DieStat<?>> diceStats) {
		diceStats.add(new SingleDieStat(DieBase.D6, TeamMapping.OPPONENT_TEAM_FOR_PLAYER, fPlayerId, roll, 4, getId(), isSuccessful()));
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.ROLL.addTo(jsonObject, roll);
		IJsonOption.SUCCESSFUL.addTo(jsonObject, successful);
		return jsonObject;
	}

	public ReportThrowAtPlayer initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		roll = IJsonOption.ROLL.getFrom(source, jsonObject);
		successful = IJsonOption.SUCCESSFUL.getFrom(source, jsonObject);
		return this;
	}

}
