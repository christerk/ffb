package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
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

	public IReport transform() {
		return new ReportDoubleHiredStarPlayer(getStarPlayerName());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.STAR_PLAYER_NAME.addTo(jsonObject, fStarPlayerName);
		return jsonObject;
	}

	public ReportDoubleHiredStarPlayer initFrom(JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
		fStarPlayerName = IJsonOption.STAR_PLAYER_NAME.getFrom(jsonObject);
		return this;
	}

}
