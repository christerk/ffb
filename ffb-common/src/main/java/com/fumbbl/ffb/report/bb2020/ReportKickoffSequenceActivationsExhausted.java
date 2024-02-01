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
public class ReportKickoffSequenceActivationsExhausted extends NoDiceReport {
	private boolean limitReached;

	public ReportKickoffSequenceActivationsExhausted() {
	}

	public ReportKickoffSequenceActivationsExhausted(boolean limitReached) {
		this.limitReached = limitReached;
	}

	@Override
	public ReportId getId() {
		return ReportId.KICKOFF_SEQUENCE_ACTIVATIONS_EXHAUSTED;
	}

	public boolean isLimitReached() {
		return limitReached;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportKickoffSequenceActivationsExhausted(limitReached);
	}

	@Override
	public Object initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		limitReached = IJsonOption.LIMIT_REACHED.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.LIMIT_REACHED.addTo(jsonObject, limitReached);
		return jsonObject;
	}
}
