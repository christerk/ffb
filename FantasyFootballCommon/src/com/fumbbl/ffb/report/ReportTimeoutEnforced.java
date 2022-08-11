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
public class ReportTimeoutEnforced extends NoDiceReport {

	private String fCoach;

	public ReportTimeoutEnforced() {
		super();
	}

	public ReportTimeoutEnforced(String pCoach) {
		fCoach = pCoach;
	}

	public ReportId getId() {
		return ReportId.TIMEOUT_ENFORCED;
	}

	public String getCoach() {
		return fCoach;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportTimeoutEnforced(getCoach());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.COACH.addTo(jsonObject, fCoach);
		return jsonObject;
	}

	public ReportTimeoutEnforced initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fCoach = IJsonOption.COACH.getFrom(source, jsonObject);
		return this;
	}

}
