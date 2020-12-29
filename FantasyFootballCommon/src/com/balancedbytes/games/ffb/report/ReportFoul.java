package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportFoul implements IReport {

	private String fDefenderId;

	public ReportFoul() {
		super();
	}

	public ReportFoul(String pDefenderId) {
		fDefenderId = pDefenderId;
	}

	public ReportId getId() {
		return ReportId.FOUL;
	}

	public String getDefenderId() {
		return fDefenderId;
	}

	// transformation

	public IReport transform() {
		return new ReportFoul(getDefenderId());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
		return jsonObject;
	}

	public ReportFoul initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fDefenderId = IJsonOption.DEFENDER_ID.getFrom(game, jsonObject);
		return this;
	}

}
