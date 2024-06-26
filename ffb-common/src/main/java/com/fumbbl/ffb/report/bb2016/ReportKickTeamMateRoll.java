package com.fumbbl.ffb.report.bb2016;

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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public class ReportKickTeamMateRoll implements IReport {

	private String fKickingPlayerId;
	private String fKickedPlayerId;
	private int fKickDistance;
	private boolean fSuccessful;
	private boolean fReRolled;
	private int[] fRoll;

	public ReportKickTeamMateRoll() {
		fRoll = new int[2];
	}

	@Override
	public ReportId getId() {
		return ReportId.KICK_TEAM_MATE_ROLL;
	}

	public ReportKickTeamMateRoll(String pKickingPlayerId, String pKickedPlayerId, boolean pSuccessful, int[] pRoll,
			boolean pReRolled, int pKickDistance) {
		fKickingPlayerId = pKickingPlayerId;
		fKickedPlayerId = pKickedPlayerId;
		fKickDistance = pKickDistance;
		fSuccessful = pSuccessful;
		fReRolled = pReRolled;
		fRoll = pRoll;
	}

	public String getKickingPlayerId() {
		return fKickingPlayerId;
	}

	public String getKickedPlayerId() {
		return fKickedPlayerId;
	}

	public int getKickDistance() {
		return fKickDistance;
	}

	public boolean isSuccessful() {
		return fSuccessful;
	}

	public int[] getRoll() {
		return fRoll;
	}

	public boolean isReRolled() {
		return fReRolled;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportKickTeamMateRoll(getKickingPlayerId(), getKickedPlayerId(), isSuccessful(), getRoll(),
				isReRolled(), getKickDistance());
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fKickingPlayerId);
		IJsonOption.DEFENDER_ID.addTo(jsonObject, fKickedPlayerId);
		IJsonOption.DISTANCE.addTo(jsonObject, fKickDistance);
		IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
		IJsonOption.ROLLS.addTo(jsonObject, fRoll);
		IJsonOption.RE_ROLLED.addTo(jsonObject, fReRolled);
		return jsonObject;
	}

	@Override
	public ReportKickTeamMateRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fKickingPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fKickedPlayerId = IJsonOption.DEFENDER_ID.getFrom(source, jsonObject);
		fKickDistance = IJsonOption.DISTANCE.getFrom(source, jsonObject);
		fSuccessful = IJsonOption.SUCCESSFUL.getFrom(source, jsonObject);
		fRoll = IJsonOption.ROLLS.getFrom(source, jsonObject);
		fReRolled = IJsonOption.RE_ROLLED.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public void addStats(Game game, List<DieStat<?>> diceStats) {
		diceStats.add(new DicePoolStat(DieBase.D6, TeamMapping.TEAM_FOR_PLAYER, fKickedPlayerId, Arrays.stream(fRoll).boxed().collect(Collectors.toList())));
	}
}
