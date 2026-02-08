package com.fumbbl.ffb.report.bb2025;

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

@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportPuntDistance extends NoDiceReport {

	private int roll;
	private boolean outOfBounds;

	@SuppressWarnings("unused")
	public ReportPuntDistance() {
		super();
	}

	public ReportPuntDistance(int roll, boolean outOfBounds) {
		this.roll = roll;
		this.outOfBounds = outOfBounds;
	}

	public ReportId getId() {
		return ReportId.PUNT_DISTANCE_ROLL;
	}


	public int getRoll() {
		return roll;
	}

	public boolean isOutOfBounds() {
		return outOfBounds;
	}
// transformation

	public IReport transform(IFactorySource source) {
		return new ReportPuntDistance(roll, outOfBounds);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.ROLL.addTo(jsonObject, roll);
		IJsonOption.OUT_OF_BOUNDS.addTo(jsonObject, outOfBounds);
		return jsonObject;
	}

	public ReportPuntDistance initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		roll = IJsonOption.ROLL.getFrom(source, jsonObject);
		outOfBounds = IJsonOption.OUT_OF_BOUNDS.getFrom(source, jsonObject);
		return this;
	}

}
