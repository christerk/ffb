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
public class ReportDoubleHiredStaff extends NoDiceReport {

	private String staffName;

	public ReportDoubleHiredStaff() {
		super();
	}

	public ReportDoubleHiredStaff(String staffName) {
		this.staffName = staffName;
	}

	public ReportId getId() {
		return ReportId.DOUBLE_HIRED_STAFF;
	}

	public String getStaffName() {
		return staffName;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportDoubleHiredStaff(getStaffName());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.NAME.addTo(jsonObject, staffName);
		return jsonObject;
	}

	public ReportDoubleHiredStaff initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		staffName = IJsonOption.NAME.getFrom(source, jsonObject);
		return this;
	}

}
