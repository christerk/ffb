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
public class ReportBlitzRoll implements IReport {

	private String teamId;
	private int amount;
	private int roll;

	public ReportBlitzRoll() {
	}

	public ReportBlitzRoll(String teamId, int roll, int amount) {
		this.teamId = teamId;
		this.amount = amount;
		this.roll = roll;
	}

	@Override
	public ReportId getId() {
		return ReportId.BLITZ_ROLL;
	}

	public String getTeamId() {
		return teamId;
	}

	public int getAmount() {
		return amount;
	}

	public int getRoll() {
		return roll;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportBlitzRoll(teamId, roll, amount);
	}

	@Override
	public Object initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		amount = IJsonOption.NR_OF_PLAYERS.getFrom(game, jsonObject);
		roll = IJsonOption.ROLL.getFrom(game, jsonObject);
		teamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.NR_OF_PLAYERS.addTo(jsonObject, amount);
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.ROLL.addTo(jsonObject, roll);
		return jsonObject;
	}
}
