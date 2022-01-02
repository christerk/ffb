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
public class ReportSelectBlitzTarget implements IReport {

	private String attacker, defender;

	public ReportSelectBlitzTarget() {
	}

	public ReportSelectBlitzTarget(String attacker, String defender) {
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
	public ReportSelectBlitzTarget initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		attacker = IJsonOption.ATTACKER_ID.getFrom(game, jsonObject);
		defender = IJsonOption.DEFENDER_ID.getFrom(game, jsonObject);
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
		return ReportId.SELECT_BLITZ_TARGET;
	}

	@Override
	public ReportSelectBlitzTarget transform(IFactorySource source) {
		return new ReportSelectBlitzTarget(attacker, defender);
	}
}
