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
public class ReportRaiseDead implements IReport {

	private String fPlayerId;
	private boolean fNurglesRot;

	public ReportRaiseDead() {
		super();
	}

	public ReportRaiseDead(String pPlayerId, boolean pNurglesRot) {
		fPlayerId = pPlayerId;
		fNurglesRot = pNurglesRot;
	}

	public ReportId getId() {
		return ReportId.RAISE_DEAD;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public boolean isNurglesRot() {
		return fNurglesRot;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportRaiseDead(getPlayerId(), isNurglesRot());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.NURGLES_ROT.addTo(jsonObject, fNurglesRot);
		return jsonObject;
	}

	public ReportRaiseDead initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fNurglesRot = IJsonOption.NURGLES_ROT.getFrom(source, jsonObject);
		return this;
	}

}
