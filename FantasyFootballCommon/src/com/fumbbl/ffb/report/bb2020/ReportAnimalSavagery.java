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
public class ReportAnimalSavagery implements IReport {
	private String attackerId, defenderId;

	public ReportAnimalSavagery() {
	}

	public ReportAnimalSavagery(String attackerId) {
		this.attackerId = attackerId;
	}

	public ReportAnimalSavagery(String attackerId, String defenderId) {
		this.attackerId = attackerId;
		this.defenderId = defenderId;
	}

	public String getAttackerId() {
		return attackerId;
	}

	public String getDefenderId() {
		return defenderId;
	}

	@Override
	public ReportAnimalSavagery initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		attackerId = IJsonOption.ATTACKER_ID.getFrom(game, jsonObject);
		defenderId = IJsonOption.DEFENDER_ID.getFrom(game, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.ATTACKER_ID.addTo(jsonObject, attackerId);
		IJsonOption.DEFENDER_ID.addTo(jsonObject, defenderId);
		return jsonObject;
	}

	@Override
	public ReportId getId() {
		return ReportId.ANIMAL_SAVAGERY;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportAnimalSavagery(attackerId, defenderId);
	}
}
