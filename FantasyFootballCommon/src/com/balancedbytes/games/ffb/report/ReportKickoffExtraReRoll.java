package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.KickoffResult;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportKickoffExtraReRoll implements IReport {

	private KickoffResult fKickoffResult;
	private int fRollHome;
	private boolean fHomeGainsReRoll;
	private int fRollAway;
	private boolean fAwayGainsReRoll;

	public ReportKickoffExtraReRoll() {
		super();
	}

	public ReportKickoffExtraReRoll(KickoffResult pKickoffResult, int pRollHome, boolean pHomeGainsReRoll, int pRollAway,
			boolean pAwayGainsReRoll) {
		fKickoffResult = pKickoffResult;
		fRollHome = pRollHome;
		fHomeGainsReRoll = pHomeGainsReRoll;
		fRollAway = pRollAway;
		fAwayGainsReRoll = pAwayGainsReRoll;
	}

	public ReportId getId() {
		return ReportId.KICKOFF_EXTRA_REROLL;
	}

	public KickoffResult getKickoffResult() {
		return fKickoffResult;
	}

	public int getRollHome() {
		return fRollHome;
	}

	public boolean isHomeGainsReRoll() {
		return fHomeGainsReRoll;
	}

	public int getRollAway() {
		return fRollAway;
	}

	public boolean isAwayGainsReRoll() {
		return fAwayGainsReRoll;
	}

	// transformation

	public IReport transform() {
		return new ReportKickoffExtraReRoll(getKickoffResult(), getRollAway(), isAwayGainsReRoll(), getRollHome(),
				isHomeGainsReRoll());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.KICKOFF_RESULT.addTo(jsonObject, fKickoffResult);
		IJsonOption.ROLL_HOME.addTo(jsonObject, fRollHome);
		IJsonOption.HOME_GAINS_RE_ROLL.addTo(jsonObject, fHomeGainsReRoll);
		IJsonOption.ROLL_AWAY.addTo(jsonObject, fRollAway);
		IJsonOption.AWAY_GAINS_RE_ROLL.addTo(jsonObject, fAwayGainsReRoll);
		return jsonObject;
	}

	public ReportKickoffExtraReRoll initFrom(JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
		fKickoffResult = (KickoffResult) IJsonOption.KICKOFF_RESULT.getFrom(jsonObject);
		fRollHome = IJsonOption.ROLL_HOME.getFrom(jsonObject);
		fHomeGainsReRoll = IJsonOption.HOME_GAINS_RE_ROLL.getFrom(jsonObject);
		fRollAway = IJsonOption.ROLL_AWAY.getFrom(jsonObject);
		fAwayGainsReRoll = IJsonOption.AWAY_GAINS_RE_ROLL.getFrom(jsonObject);
		return this;
	}

}
