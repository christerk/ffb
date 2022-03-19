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
public class ReportBlock implements IReport {

	private String fDefenderId;

	public ReportBlock() {
		super();
	}

	public ReportBlock(String pDefenderId) {
		fDefenderId = pDefenderId;
	}

	public ReportId getId() {
		return ReportId.BLOCK;
	}

	public String getDefenderId() {
		return fDefenderId;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportBlock(getDefenderId());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
		return jsonObject;
	}

	public ReportBlock initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fDefenderId = IJsonOption.DEFENDER_ID.getFrom(source, jsonObject);
		return this;
	}

}
