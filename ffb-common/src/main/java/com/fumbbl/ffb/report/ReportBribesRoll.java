package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.stats.DieBase;
import com.fumbbl.ffb.stats.DieStat;
import com.fumbbl.ffb.stats.SingleDieStat;
import com.fumbbl.ffb.stats.TeamMapping;

import java.util.List;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportBribesRoll implements IReport {

	private String fPlayerId;
	private boolean fSuccessful;
	private int fRoll;

	public ReportBribesRoll() {
		super();
	}

	public ReportBribesRoll(String pPlayerId, boolean pSuccessful, int pRoll) {
		fPlayerId = pPlayerId;
		fSuccessful = pSuccessful;
		fRoll = pRoll;
	}

	public ReportId getId() {
		return ReportId.BRIBES_ROLL;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public boolean isSuccessful() {
		return fSuccessful;
	}

	public int getRoll() {
		return fRoll;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportBribesRoll(getPlayerId(), isSuccessful(), getRoll());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
		IJsonOption.ROLL.addTo(jsonObject, fRoll);
		return jsonObject;
	}

	public ReportBribesRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fSuccessful = IJsonOption.SUCCESSFUL.getFrom(source, jsonObject);
		fRoll = IJsonOption.ROLL.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public void addStats(Game game, List<DieStat<?>> diceStats) {
		diceStats.add(new SingleDieStat(DieBase.D6, TeamMapping.TEAM_FOR_PLAYER, fPlayerId, fRoll, 2, getId(), fSuccessful));
	}
}
