package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportBrilliantCoachingReRollsLost implements IReport {

	private String teamId;
	private int amount;

	public ReportBrilliantCoachingReRollsLost() {
	}

	public ReportBrilliantCoachingReRollsLost(String teamId, int amount) {
		this.teamId = teamId;
		this.amount = amount;
	}

	@Override
	public ReportId getId() {
		return ReportId.BRILLIANT_COACHING_REROLLS_LOST;
	}

	public String getTeamId() {
		return teamId;
	}

	public int getAmount() {
		return amount;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportBrilliantCoachingReRollsLost(teamId, amount);
	}

	@Override
	public Object initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		teamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		amount = IJsonOption.RE_ROLLS_BRILLIANT_COACHING_ONE_DRIVE.getFrom(game, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.RE_ROLLS_BRILLIANT_COACHING_ONE_DRIVE.addTo(jsonObject, amount);
		return jsonObject;
	}
}
