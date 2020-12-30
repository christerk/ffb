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
public class ReportPilingOn implements IReport {

	private String fPlayerId;
	private boolean fUsed;
	private boolean fReRollInjury;

	public ReportPilingOn() {
		super();
	}

	public ReportPilingOn(String pPlayerId, boolean pUsed, boolean pReRollInjury) {
		fPlayerId = pPlayerId;
		fUsed = pUsed;
		fReRollInjury = pReRollInjury;
	}

	public ReportId getId() {
		return ReportId.PILING_ON;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public boolean isUsed() {
		return fUsed;
	}

	public boolean isReRollInjury() {
		return fReRollInjury;
	}

	// transformation

	public IReport transform(Game game) {
		return new ReportPilingOn(getPlayerId(), isUsed(), isReRollInjury());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.USED.addTo(jsonObject, fUsed);
		IJsonOption.RE_ROLL_INJURY.addTo(jsonObject, fReRollInjury);
		return jsonObject;
	}

	public ReportPilingOn initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		fUsed = IJsonOption.USED.getFrom(game, jsonObject);
		fReRollInjury = IJsonOption.RE_ROLL_INJURY.getFrom(game, jsonObject);
		return this;
	}

}
