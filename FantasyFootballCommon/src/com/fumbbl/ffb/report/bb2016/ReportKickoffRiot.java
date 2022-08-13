package com.fumbbl.ffb.report.bb2016;

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
@RulesCollection(RulesCollection.Rules.BB2016)
public class ReportKickoffRiot extends NoDiceReport {

	private int fRoll;
	private int fTurnModifier;

	public ReportKickoffRiot() {
		super();
	}

	public ReportKickoffRiot(int pRoll, int pTurnModifier) {
		fRoll = pRoll;
		fTurnModifier = pTurnModifier;
	}

	public ReportId getId() {
		return ReportId.KICKOFF_RIOT;
	}

	public int getRoll() {
		return fRoll;
	}

	public int getTurnModifier() {
		return fTurnModifier;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportKickoffRiot(getRoll(), getTurnModifier());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.ROLL.addTo(jsonObject, fRoll);
		IJsonOption.TURN_MODIFIER.addTo(jsonObject, fTurnModifier);
		return jsonObject;
	}

	public ReportKickoffRiot initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fRoll = IJsonOption.ROLL.getFrom(source, jsonObject);
		fTurnModifier = IJsonOption.TURN_MODIFIER.getFrom(source, jsonObject);
		return this;
	}

}
