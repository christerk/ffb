package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportNoPlayersToField implements IReport {

	private String fTeamId;

	public ReportNoPlayersToField() {
		super();
	}

	public ReportNoPlayersToField(String pTeamId) {
		fTeamId = pTeamId;
	}

	public ReportId getId() {
		return ReportId.NO_PLAYERS_TO_FIELD;
	}

	public String getTeamId() {
		return fTeamId;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportNoPlayersToField(getTeamId());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		return jsonObject;
	}

	public ReportNoPlayersToField initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		return this;
	}

}
