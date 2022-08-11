package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportHandOver extends NoDiceReport {

	private String fCatcherId;

	public ReportHandOver() {
		super();
	}

	public ReportHandOver(String pCatcherId) {
		fCatcherId = pCatcherId;
	}

	public ReportId getId() {
		return ReportId.HAND_OVER;
	}

	public String getCatcherId() {
		return fCatcherId;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportHandOver(getCatcherId());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.CATCHER_ID.addTo(jsonObject, fCatcherId);
		return jsonObject;
	}

	public ReportHandOver initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fCatcherId = IJsonOption.CATCHER_ID.getFrom(source, jsonObject);
		return this;
	}

}
