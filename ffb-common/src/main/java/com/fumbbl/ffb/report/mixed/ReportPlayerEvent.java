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
public class ReportPlayerEvent extends NoDiceReport {

	private String fPlayerId, eventMessage;

	public ReportPlayerEvent() {
	}

	public ReportPlayerEvent(String fPlayerId, String eventMessage) {
		this.fPlayerId = fPlayerId;
		this.eventMessage = eventMessage;
	}

	public ReportId getId() {
		return ReportId.PLAYER_EVENT;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public String getEventMessage() {
		return eventMessage;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportPlayerEvent(getPlayerId(), eventMessage);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.MESSAGE.addTo(jsonObject, eventMessage);
		return jsonObject;
	}

	public ReportPlayerEvent initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		eventMessage = IJsonOption.MESSAGE.getFrom(source, jsonObject);
		return this;
	}

}
