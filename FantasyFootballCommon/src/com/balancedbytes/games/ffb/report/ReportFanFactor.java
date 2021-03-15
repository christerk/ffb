package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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
		teamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		dedicatedFans = IJsonOption.DEDICATED_FANS.getFrom(game, jsonObject);
		roll = IJsonOption.DEDICATED_FANS_ROLL.getFrom(game, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.DEDICATED_FANS.addTo(jsonObject, dedicatedFans);
		IJsonOption.DEDICATED_FANS_ROLL.addTo(jsonObject, roll);
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
