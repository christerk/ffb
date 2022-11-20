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

import java.util.Arrays;
import java.util.List;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportMasterChefRoll implements IReport {

	private String fTeamId;
	private int[] fMasterChefRoll;
	private int fReRollsStolen;

	public ReportMasterChefRoll() {
		super();
	}

	public ReportMasterChefRoll(String pTeamId, int[] pRoll, int pReRollsStolen) {
		fTeamId = pTeamId;
		fMasterChefRoll = pRoll;
		fReRollsStolen = pReRollsStolen;
	}

	public ReportId getId() {
		return ReportId.MASTER_CHEF_ROLL;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public int[] getMasterChefRoll() {
		return fMasterChefRoll;
	}

	public int getReRollsStolen() {
		return fReRollsStolen;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportMasterChefRoll(getTeamId(), getMasterChefRoll(), getReRollsStolen());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		IJsonOption.MASTER_CHEF_ROLL.addTo(jsonObject, fMasterChefRoll);
		IJsonOption.RE_ROLLS_STOLEN.addTo(jsonObject, fReRollsStolen);
		return jsonObject;
	}

	public ReportMasterChefRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		fMasterChefRoll = IJsonOption.MASTER_CHEF_ROLL.getFrom(source, jsonObject);
		fReRollsStolen = IJsonOption.RE_ROLLS_STOLEN.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public void addStats(Game game, List<DieStat<?>> diceStats) {
		Arrays.stream(fMasterChefRoll).mapToObj(roll ->
				new SingleDieStat(DieBase.D6, TeamMapping.TEAM, fTeamId, roll, 4, getId(), roll >= 4, false))
			.forEach(diceStats::add);
	}
}
