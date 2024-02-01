package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.NoDiceReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportKickoffTimeout extends NoDiceReport {

	private int turnModifier, turnNumber;

	public ReportKickoffTimeout() {
		super();
	}

	public ReportKickoffTimeout(int turnNumber, int turnModifier) {
		this.turnNumber = turnNumber;
		this.turnModifier = turnModifier;
	}

	public ReportId getId() {
		return ReportId.KICKOFF_TIMEOUT;
	}

	public int getTurnModifier() {
		return turnModifier;
	}

	public int getTurnNumber() {
		return turnNumber;
	}
// transformation

	public IReport transform(IFactorySource source) {
		return new ReportKickoffTimeout(turnNumber, getTurnModifier());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TURN_MODIFIER.addTo(jsonObject, turnModifier);
		IJsonOption.TURN_NR.addTo(jsonObject, turnNumber);
		return jsonObject;
	}

	public ReportKickoffTimeout initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		turnModifier = IJsonOption.TURN_MODIFIER.getFrom(source, jsonObject);
		turnNumber = IJsonOption.TURN_NR.getFrom(source, jsonObject);
		return this;
	}

}
