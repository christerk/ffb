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
public class ReportKickoffSequenceActivationsCount implements IReport {

	private int amount;
	private int available;
	private int limit;

	public ReportKickoffSequenceActivationsCount() {
	}

	public ReportKickoffSequenceActivationsCount(int available, int amount, int limit) {
		this.amount = amount;
		this.available = available;
		this.limit = limit;
	}

	@Override
	public ReportId getId() {
		return ReportId.KICKOFF_SEQUENCE_ACTIVATIONS_COUNT;
	}

	public int getAmount() {
		return amount;
	}

	public int getAvailable() {
		return available;
	}

	public int getLimit() {
		return limit;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportKickoffSequenceActivationsCount(available, amount, limit);
	}

	@Override
	public Object initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		amount = IJsonOption.NR_OF_PLAYERS.getFrom(source, jsonObject);
		limit = IJsonOption.NR_OF_PLAYERS_ALLOWED.getFrom(source, jsonObject);
		available = IJsonOption.NUMBER.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.NR_OF_PLAYERS.addTo(jsonObject, amount);
		IJsonOption.NUMBER.addTo(jsonObject, available);
		IJsonOption.NR_OF_PLAYERS_ALLOWED.addTo(jsonObject, limit);
		return jsonObject;
	}
}
