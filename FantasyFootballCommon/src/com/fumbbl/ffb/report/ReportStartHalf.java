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
public class ReportStartHalf extends NoDiceReport {

	private int fHalf;

	public ReportStartHalf() {
		super();
	}

	public ReportStartHalf(int pHalf) {
		fHalf = pHalf;
	}

	public ReportId getId() {
		return ReportId.START_HALF;
	}

	public int getHalf() {
		return fHalf;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportStartHalf(getHalf());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.HALF.addTo(jsonObject, fHalf);
		return jsonObject;
	}

	public ReportStartHalf initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fHalf = IJsonOption.HALF.getFrom(source, jsonObject);
		return this;
	}

}
