package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportPilingOn implements IReport {

	private String fPlayerId;
	private boolean fUsed;
	private boolean fReRollInjury;

	public ReportPilingOn() {
		super();
	}

	public ReportPilingOn(String pPlayerId, boolean pUsed, boolean pReRollInjury) {
		fPlayerId = pPlayerId;
		fUsed = pUsed;
		fReRollInjury = pReRollInjury;
	}

	public ReportId getId() {
		return ReportId.PILING_ON;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public boolean isUsed() {
		return fUsed;
	}

	public boolean isReRollInjury() {
		return fReRollInjury;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportPilingOn(getPlayerId(), isUsed(), isReRollInjury());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.USED.addTo(jsonObject, fUsed);
		IJsonOption.RE_ROLL_INJURY.addTo(jsonObject, fReRollInjury);
		return jsonObject;
	}

	public ReportPilingOn initFrom(IFactorySource source, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fUsed = IJsonOption.USED.getFrom(source, jsonObject);
		fReRollInjury = IJsonOption.RE_ROLL_INJURY.getFrom(source, jsonObject);
		return this;
	}

}
