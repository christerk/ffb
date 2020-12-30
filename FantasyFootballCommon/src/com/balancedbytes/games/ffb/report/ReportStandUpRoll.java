package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportStandUpRoll implements IReport {

	private String fPlayerId;
	private boolean fSuccessful;
	private int fRoll;
	private int fModifier;
	private boolean fReRolled;

	public ReportStandUpRoll() {
		super();
	}

	public ReportStandUpRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pModifier, boolean pReRolled) {
		fPlayerId = pPlayerId;
		fSuccessful = pSuccessful;
		fRoll = pRoll;
		fModifier = pModifier;
		fReRolled = pReRolled;
	}

	public ReportId getId() {
		return ReportId.STAND_UP_ROLL;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public boolean isSuccessful() {
		return fSuccessful;
	}

	public int getRoll() {
		return fRoll;
	}

	public int getModifier() {
		return fModifier;
	}

	public int getMinimumRoll() {
		return Math.max(2, 4 - fModifier);
	}

	public boolean isReRolled() {
		return fReRolled;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportStandUpRoll(getPlayerId(), isSuccessful(), getRoll(), getModifier(), isReRolled());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
		IJsonOption.ROLL.addTo(jsonObject, fRoll);
		IJsonOption.MODIFIER.addTo(jsonObject, fModifier);
		IJsonOption.RE_ROLLED.addTo(jsonObject, fReRolled);
		return jsonObject;
	}

	public ReportStandUpRoll initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		fSuccessful = IJsonOption.SUCCESSFUL.getFrom(game, jsonObject);
		fRoll = IJsonOption.ROLL.getFrom(game, jsonObject);
		fModifier = IJsonOption.MODIFIER.getFrom(game, jsonObject);
		fReRolled = IJsonOption.RE_ROLLED.getFrom(game, jsonObject);
		return this;
	}

}
