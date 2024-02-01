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
public class ReportFumblerooskie extends NoDiceReport {

	private String playerId;
	private boolean used;

	public ReportFumblerooskie() {
	}

	public ReportFumblerooskie(String playerId, boolean used) {
		this.playerId = playerId;
		this.used = used;
	}

	public String getPlayerId() {
		return playerId;
	}

	public boolean isUsed() {
		return used;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.USED.addTo(jsonObject, used);
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		return jsonObject;
	}

	public ReportFumblerooskie initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		used = IJsonOption.USED.getFrom(source, jsonObject);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}


	@Override
	public ReportId getId() {
		return ReportId.FUMBLEROOSKIE;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportFumblerooskie(playerId, used);
	}
}
