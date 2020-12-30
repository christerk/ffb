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
public class ReportKickoffRiot implements IReport {

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

	public ReportKickoffRiot initFrom(IFactorySource source, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fRoll = IJsonOption.ROLL.getFrom(source, jsonObject);
		fTurnModifier = IJsonOption.TURN_MODIFIER.getFrom(source, jsonObject);
		return this;
	}

}
