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
public class ReportBiasedRef extends NoDiceReport {
	private boolean foulSpotted;
	private int roll;

	public ReportBiasedRef() {
	}

	public ReportBiasedRef(int roll, boolean foulSpotted) {
		this.foulSpotted = foulSpotted;
		this.roll = roll;
	}

	public boolean isFoulSpotted() {
		return foulSpotted;
	}

	public int getRoll() {
		return roll;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.FOULING_PLAYER_BANNED.addTo(jsonObject, foulSpotted);
		IJsonOption.ROLL.addTo(jsonObject, roll);
		return jsonObject;
	}

	public ReportBiasedRef initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		foulSpotted = IJsonOption.FOULING_PLAYER_BANNED.getFrom(source, jsonObject);
		roll = IJsonOption.ROLL.getFrom(source, jsonObject);
		return this;
	}


	@Override
	public ReportId getId() {
		return ReportId.BIASED_REF;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportBiasedRef(roll, foulSpotted);
	}
}
