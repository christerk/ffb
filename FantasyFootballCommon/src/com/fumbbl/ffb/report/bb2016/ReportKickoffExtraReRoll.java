package com.fumbbl.ffb.report.bb2016;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.kickoff.KickoffResult;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
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
		return ReportId.KICKOFF_EXTRA_RE_ROLL;
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

	public IReport transform(IFactorySource source) {
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

	public ReportKickoffExtraReRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fKickoffResult = (KickoffResult) IJsonOption.KICKOFF_RESULT.getFrom(source, jsonObject);
		fRollHome = IJsonOption.ROLL_HOME.getFrom(source, jsonObject);
		fHomeGainsReRoll = IJsonOption.HOME_GAINS_RE_ROLL.getFrom(source, jsonObject);
		fRollAway = IJsonOption.ROLL_AWAY.getFrom(source, jsonObject);
		fAwayGainsReRoll = IJsonOption.AWAY_GAINS_RE_ROLL.getFrom(source, jsonObject);
		return this;
	}

}
