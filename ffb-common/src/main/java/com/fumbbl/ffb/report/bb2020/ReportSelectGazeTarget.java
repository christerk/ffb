package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.NoDiceReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportSelectGazeTarget extends NoDiceReport {

	private String attacker, defender;

	public ReportSelectGazeTarget() {
	}

	public ReportSelectGazeTarget(String attacker, String defender) {
		this.attacker = attacker;
		this.defender = defender;
	}

	public String getAttacker() {
		return attacker;
	}

	public String getDefender() {
		return defender;
	}

	@Override
	public ReportSelectGazeTarget initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		attacker = IJsonOption.ATTACKER_ID.getFrom(source, jsonObject);
		defender = IJsonOption.DEFENDER_ID.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.ATTACKER_ID.addTo(jsonObject, attacker);
		IJsonOption.DEFENDER_ID.addTo(jsonObject, defender);
		return jsonObject;
	}

	@Override
	public ReportId getId() {
		return ReportId.SELECT_GAZE_TARGET;
	}

	@Override
	public ReportSelectGazeTarget transform(IFactorySource source) {
		return new ReportSelectGazeTarget(attacker, defender);
	}
}
