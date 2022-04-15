package com.fumbbl.ffb.report.bb2020;

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
@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportWinnings implements IReport {

	private int winningsHome, winningsAway;

	public ReportWinnings() {
		super();
	}

	public ReportWinnings(int winningsHome, int winningsAway) {
		this.winningsHome = winningsHome;
		this.winningsAway = winningsAway;
	}

	public ReportId getId() {
		return ReportId.WINNINGS;
	}

	public int getWinningsHome() {
		return winningsHome;
	}

	public int getWinningsAway() {
		return winningsAway;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportWinnings(getWinningsAway(), getWinningsHome());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.WINNINGS_HOME.addTo(jsonObject, winningsHome);
		IJsonOption.WINNINGS_AWAY.addTo(jsonObject, winningsAway);
		return jsonObject;
	}

	public ReportWinnings initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		winningsHome = IJsonOption.WINNINGS_HOME.getFrom(source, jsonObject);
		winningsAway = IJsonOption.WINNINGS_AWAY.getFrom(source, jsonObject);
		return this;
	}

}
