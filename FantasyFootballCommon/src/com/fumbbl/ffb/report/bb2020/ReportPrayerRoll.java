package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportPrayerRoll implements IReport {

	private int roll;

	public ReportPrayerRoll() {
		super();
	}

	public ReportPrayerRoll(int roll) {
		this.roll = roll;
	}

	public ReportId getId() {
		return ReportId.PRAYER_ROLL;
	}

	public int getRoll() {
		return roll;
	}
// transformation

	public IReport transform(IFactorySource source) {
		return new ReportPrayerRoll(roll);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.ROLL.addTo(jsonObject, roll);
		return jsonObject;
	}

	public ReportPrayerRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		roll = IJsonOption.ROLL.getFrom(source, jsonObject);
		return this;
	}

}
