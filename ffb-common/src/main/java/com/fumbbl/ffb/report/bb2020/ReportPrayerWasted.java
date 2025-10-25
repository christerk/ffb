package com.fumbbl.ffb.report.bb2020;

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
public class ReportPrayerWasted extends NoDiceReport {

	private String name;
	private String playerId;

	public ReportPrayerWasted() {
		super();
	}

	public ReportPrayerWasted(String name) {
		this.name = name;
	}

	public ReportPrayerWasted(String name, String playerId) {
		this.name = name;
		this.playerId = playerId;
	}

	public ReportId getId() {
		return ReportId.PRAYER_WASTED;
	}

	public String getPrayerName() {
		return name;
	}

	public String getPlayerId() {
		return playerId;
	}
// transformation

	public IReport transform(IFactorySource source) {
		return new ReportPrayerWasted(name, playerId);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.NAME.addTo(jsonObject, name);
		return jsonObject;
	}

	public ReportPrayerWasted initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		name = IJsonOption.NAME.getFrom(source, jsonObject);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}

}
