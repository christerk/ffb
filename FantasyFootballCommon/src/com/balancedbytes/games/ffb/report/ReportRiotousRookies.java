package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportRiotousRookies implements IReport {

	private int[] roll;
	private int amount;
	private String teamId;

	public ReportRiotousRookies() {
	}

	public ReportRiotousRookies(int[] roll, int amount, String teamId) {
		this.roll = roll;
		this.amount = amount;
		this.teamId = teamId;
	}

	@Override
	public ReportId getId() {
		return ReportId.RIOTOUS_ROOKIES;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportRiotousRookies(roll, amount, teamId);
	}

	public int[] getRoll() {
		return roll;
	}

	public int getAmount() {
		return amount;
	}

	public String getTeamId() {
		return teamId;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.RIOTOUS_ROLL.addTo(jsonObject, roll);
		IJsonOption.RIOTOUS_AMOUNT.addTo(jsonObject, amount);
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		return jsonObject;
	}

	public ReportRiotousRookies initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		roll = IJsonOption.RIOTOUS_ROLL.getFrom(game, jsonObject);
		amount = IJsonOption.RIOTOUS_AMOUNT.getFrom(game, jsonObject);
		teamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		return this;
	}
}
