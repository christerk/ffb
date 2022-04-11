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
	public ReportFanFactor initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		dedicatedFans = IJsonOption.DEDICATED_FANS.getFrom(source, jsonObject);
		roll = IJsonOption.DEDICATED_FANS_ROLL.getFrom(source, jsonObject);
		result = IJsonOption.DEDICATED_FANS_RESULT.getFrom(source, jsonObject);
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
