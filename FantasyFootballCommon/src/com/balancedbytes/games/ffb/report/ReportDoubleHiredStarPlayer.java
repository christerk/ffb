package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportDoubleHiredStarPlayer implements IReport {

	private String fStarPlayerName;

	public ReportDoubleHiredStarPlayer() {
		super();
	}

	public ReportDoubleHiredStarPlayer(String pStarPlayerName) {
		fStarPlayerName = pStarPlayerName;
	}

	public ReportId getId() {
		return ReportId.DOUBLE_HIRED_STAR_PLAYER;
	}

	public String getStarPlayerName() {
		return fStarPlayerName;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportDoubleHiredStarPlayer(getStarPlayerName());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.STAR_PLAYER_NAME.addTo(jsonObject, fStarPlayerName);
		return jsonObject;
	}

	public ReportDoubleHiredStarPlayer initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fStarPlayerName = IJsonOption.STAR_PLAYER_NAME.getFrom(game, jsonObject);
		return this;
	}

}
