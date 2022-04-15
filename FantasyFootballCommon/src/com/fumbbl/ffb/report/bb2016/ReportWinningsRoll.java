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

	public ReportWinningsRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fWinningsRollHome = IJsonOption.WINNINGS_ROLL_HOME.getFrom(source, jsonObject);
		fWinningsHome = IJsonOption.WINNINGS_HOME.getFrom(source, jsonObject);
		fWinningsRollAway = IJsonOption.WINNINGS_ROLL_AWAY.getFrom(source, jsonObject);
		fWinningsAway = IJsonOption.WINNINGS_AWAY.getFrom(source, jsonObject);
		return this;
	}

}
