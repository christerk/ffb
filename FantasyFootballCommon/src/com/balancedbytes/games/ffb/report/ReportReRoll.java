package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportReRoll implements IReport {

	private String fPlayerId;
	private ReRollSource fReRollSource;
	private boolean fSuccessful;
	private int fRoll;

	public ReportReRoll() {
		super();
	}

	public ReportReRoll(String pPlayerId, ReRollSource pReRollSource, boolean pSuccessful, int pRoll) {
		fPlayerId = pPlayerId;
		fReRollSource = pReRollSource;
		fSuccessful = pSuccessful;
		fRoll = pRoll;
	}

	public ReportId getId() {
		return ReportId.RE_ROLL;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public ReRollSource getReRollSource() {
		return fReRollSource;
	}

	public boolean isSuccessful() {
		return fSuccessful;
	}

	public int getRoll() {
		return fRoll;
	}

	// transformation

	public IReport transform(Game game) {
		return new ReportReRoll(getPlayerId(), getReRollSource(), isSuccessful(), getRoll());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.RE_ROLL_SOURCE.addTo(jsonObject, fReRollSource);
		IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
		IJsonOption.ROLL.addTo(jsonObject, fRoll);
		return jsonObject;
	}

	public ReportReRoll initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		fReRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE.getFrom(game, jsonObject);
		fSuccessful = IJsonOption.SUCCESSFUL.getFrom(game, jsonObject);
		fRoll = IJsonOption.ROLL.getFrom(game, jsonObject);
		return this;
	}

}
