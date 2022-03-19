package com.fumbbl.ffb.report.bb2016;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
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

	public IReport transform(IFactorySource source) {
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

	public ReportPenaltyShootout initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fRollHome = IJsonOption.ROLL_HOME.getFrom(source, jsonObject);
		fReRollsLeftHome = IJsonOption.RE_ROLLS_LEFT_HOME.getFrom(source, jsonObject);
		fRollAway = IJsonOption.ROLL_AWAY.getFrom(source, jsonObject);
		fReRollsLeftAway = IJsonOption.RE_ROLLS_LEFT_AWAY.getFrom(source, jsonObject);
		return this;
	}

}
