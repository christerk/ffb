package com.balancedbytes.games.ffb.report.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.report.IReport;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.UtilReport;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportFanFactor implements IReport {
	private int roll, dedicatedFans, result;
	private String teamId;

	public ReportFanFactor() {
	}

	public ReportFanFactor(String teamId, int roll, int dedicatedFans) {
		this.roll = roll;
		this.dedicatedFans = dedicatedFans;
		this.teamId = teamId;
		this.result = roll + dedicatedFans;
	}

	public int getRoll() {
		return roll;
	}

	public void setRoll(int roll) {
		this.roll = roll;
	}

	public int getDedicatedFans() {
		return dedicatedFans;
	}

	public void setDedicatedFans(int dedicatedFans) {
		this.dedicatedFans = dedicatedFans;
	}

	public int getResult() {
		return result;
	}

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	@Override
	public ReportFanFactor initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		teamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		dedicatedFans = IJsonOption.DEDICATED_FANS.getFrom(game, jsonObject);
		roll = IJsonOption.DEDICATED_FANS_ROLL.getFrom(game, jsonObject);
		result = IJsonOption.DEDICATED_FANS_RESULT.getFrom(game, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.DEDICATED_FANS.addTo(jsonObject, dedicatedFans);
		IJsonOption.DEDICATED_FANS_ROLL.addTo(jsonObject, roll);
		IJsonOption.DEDICATED_FANS_RESULT.addTo(jsonObject, result);
		return jsonObject;
	}

	@Override
	public ReportId getId() {
		return ReportId.FAN_FACTOR;
	}

	@Override
	public ReportFanFactor transform(IFactorySource source) {
		return new ReportFanFactor(teamId, roll, dedicatedFans);
	}
}
