package com.fumbbl.ffb.report.bb2016;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;
import com.fumbbl.ffb.stats.DieBase;
import com.fumbbl.ffb.stats.DieStat;
import com.fumbbl.ffb.stats.SingleDiceStat;
import com.fumbbl.ffb.stats.TeamMapping;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public class ReportApothecaryRoll implements IReport {

	private String fPlayerId;
	private int[] fCasualtyRoll;
	private PlayerState fPlayerState;
	private SeriousInjury fSeriousInjury;

	public ReportApothecaryRoll() {
		super();
	}

	public ReportApothecaryRoll(String pPlayerId, int[] pCasualtyRoll, PlayerState pPlayerState,
			SeriousInjury pSeriousInjury) {
		fPlayerId = pPlayerId;
		fCasualtyRoll = pCasualtyRoll;
		fPlayerState = pPlayerState;
		fSeriousInjury = pSeriousInjury;
	}

	public ReportId getId() {
		return ReportId.APOTHECARY_ROLL;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public int[] getCasualtyRoll() {
		return fCasualtyRoll;
	}

	public PlayerState getPlayerState() {
		return fPlayerState;
	}

	public SeriousInjury getSeriousInjury() {
		return fSeriousInjury;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportApothecaryRoll(getPlayerId(), getCasualtyRoll(), getPlayerState(), getSeriousInjury());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.CASUALTY_ROLL.addTo(jsonObject, fCasualtyRoll);
		IJsonOption.PLAYER_STATE.addTo(jsonObject, fPlayerState);
		IJsonOption.SERIOUS_INJURY.addTo(jsonObject, fSeriousInjury);
		return jsonObject;
	}

	public ReportApothecaryRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fCasualtyRoll = IJsonOption.CASUALTY_ROLL.getFrom(source, jsonObject);
		fPlayerState = IJsonOption.PLAYER_STATE.getFrom(source, jsonObject);
		fSeriousInjury = (SeriousInjury) IJsonOption.SERIOUS_INJURY.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public void addStats(Game game, List<DieStat<?>> diceStats) {
		if (ArrayTool.isProvided(fCasualtyRoll)) {
			diceStats.add(new SingleDiceStat(DieBase.D6, TeamMapping.OPPONENT_TEAM_FOR_PLAYER, fPlayerId, Collections.singletonList(fCasualtyRoll[0])));
		}
	}
}
