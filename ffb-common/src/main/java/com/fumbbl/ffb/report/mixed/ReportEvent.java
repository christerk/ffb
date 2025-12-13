package com.fumbbl.ffb.report.mixed;

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

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportEvent extends NoDiceReport {

	private String eventMessage;

	public ReportEvent() {
	}

	public ReportEvent(String eventMessage) {
		this.eventMessage = eventMessage;
	}

	public ReportId getId() {
		return ReportId.EVENT;
	}

	public String getEventMessage() {
		return eventMessage;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportEvent(eventMessage);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.MESSAGE.addTo(jsonObject, eventMessage);
		return jsonObject;
	}

	public ReportEvent initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		eventMessage = IJsonOption.MESSAGE.getFrom(source, jsonObject);
		return this;
	}

}
