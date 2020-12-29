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
public class ReportPenaltyShootout implements IReport {

	private int fRollHome;
	private int fReRollsLeftHome;
	private int fRollAway;
	private int fReRollsLeftAway;

	public ReportPenaltyShootout() {
		super();
	}

	public ReportPenaltyShootout(int pRollHome, int pReRollsLeftHome, int pRollAway, int pReRollsLeftAway) {
		fRollHome = pRollHome;
		fReRollsLeftHome = pReRollsLeftHome;
		fRollAway = pRollAway;
		fReRollsLeftAway = pReRollsLeftAway;
	}

	public ReportId getId() {
		return ReportId.PENALTY_SHOOTOUT;
	}

	public int getRollHome() {
		return fRollHome;
	}

	public int getReRollsLeftHome() {
		return fReRollsLeftHome;
	}

	public int getRollAway() {
		return fRollAway;
	}

	public int getReRollsLeftAway() {
		return fReRollsLeftAway;
	}

	// transformation

	public IReport transform(Game game) {
		return new ReportPenaltyShootout(getRollAway(), getReRollsLeftAway(), getRollHome(), getReRollsLeftHome());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.ROLL_HOME.addTo(jsonObject, fRollHome);
		IJsonOption.RE_ROLLS_LEFT_HOME.addTo(jsonObject, fReRollsLeftHome);
		IJsonOption.ROLL_AWAY.addTo(jsonObject, fRollAway);
		IJsonOption.RE_ROLLS_LEFT_AWAY.addTo(jsonObject, fReRollsLeftAway);
		return jsonObject;
	}

	public ReportPenaltyShootout initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fRollHome = IJsonOption.ROLL_HOME.getFrom(game, jsonObject);
		fReRollsLeftHome = IJsonOption.RE_ROLLS_LEFT_HOME.getFrom(game, jsonObject);
		fRollAway = IJsonOption.ROLL_AWAY.getFrom(game, jsonObject);
		fReRollsLeftAway = IJsonOption.RE_ROLLS_LEFT_AWAY.getFrom(game, jsonObject);
		return this;
	}

}
