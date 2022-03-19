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

	public ReportDoubleHiredStarPlayer initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fStarPlayerName = IJsonOption.STAR_PLAYER_NAME.getFrom(source, jsonObject);
		return this;
	}

}
