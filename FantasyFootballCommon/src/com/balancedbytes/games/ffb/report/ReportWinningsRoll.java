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
public class ReportWinningsRoll implements IReport {

	private int fWinningsRollHome;
	private int fWinningsHome;
	private int fWinningsRollAway;
	private int fWinningsAway;

	public ReportWinningsRoll() {
		super();
	}

	public ReportWinningsRoll(int pRollHome, int pWinningsHome, int pRollAway, int pWinningsAway) {
		fWinningsRollHome = pRollHome;
		fWinningsHome = pWinningsHome;
		fWinningsRollAway = pRollAway;
		fWinningsAway = pWinningsAway;
	}

	public ReportId getId() {
		return ReportId.WINNINGS_ROLL;
	}

	public int getWinningsRollHome() {
		return fWinningsRollHome;
	}

	public int getWinningsHome() {
		return fWinningsHome;
	}

	public int getWinningsRollAway() {
		return fWinningsRollAway;
	}

	public int getWinningsAway() {
		return fWinningsAway;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportWinningsRoll(getWinningsRollAway(), getWinningsAway(), getWinningsRollHome(), getWinningsHome());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.WINNINGS_ROLL_HOME.addTo(jsonObject, fWinningsRollHome);
		IJsonOption.WINNINGS_HOME.addTo(jsonObject, fWinningsHome);
		IJsonOption.WINNINGS_ROLL_AWAY.addTo(jsonObject, fWinningsRollAway);
		IJsonOption.WINNINGS_AWAY.addTo(jsonObject, fWinningsAway);
		return jsonObject;
	}

	public ReportWinningsRoll initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fWinningsRollHome = IJsonOption.WINNINGS_ROLL_HOME.getFrom(game, jsonObject);
		fWinningsHome = IJsonOption.WINNINGS_HOME.getFrom(game, jsonObject);
		fWinningsRollAway = IJsonOption.WINNINGS_ROLL_AWAY.getFrom(game, jsonObject);
		fWinningsAway = IJsonOption.WINNINGS_AWAY.getFrom(game, jsonObject);
		return this;
	}

}
