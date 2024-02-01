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
public class ReportBombOutOfBounds extends NoDiceReport {

	public ReportBombOutOfBounds() {
		super();
	}

	public ReportId getId() {
		return ReportId.BOMB_OUT_OF_BOUNDS;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportBombOutOfBounds();
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		return jsonObject;
	}

	public ReportBombOutOfBounds initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		return this;
	}

}
