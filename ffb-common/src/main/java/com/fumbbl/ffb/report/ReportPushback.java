package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PushbackMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportPushback extends NoDiceReport {

	private String fDefenderId;
	private PushbackMode fPushbackMode;

	public ReportPushback() {
		super();
	}

	public ReportPushback(String pDefenderId, PushbackMode pMode) {
		this();
		fDefenderId = pDefenderId;
		fPushbackMode = pMode;
	}

	public ReportId getId() {
		return ReportId.PUSHBACK;
	}

	public String getDefenderId() {
		return fDefenderId;
	}

	public PushbackMode getPushbackMode() {
		return fPushbackMode;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportPushback(getDefenderId(), getPushbackMode());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
		IJsonOption.PUSHBACK_MODE.addTo(jsonObject, fPushbackMode);
		return jsonObject;
	}

	public ReportPushback initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fDefenderId = IJsonOption.DEFENDER_ID.getFrom(source, jsonObject);
		fPushbackMode = (PushbackMode) IJsonOption.PUSHBACK_MODE.getFrom(source, jsonObject);
		return this;
	}

}
