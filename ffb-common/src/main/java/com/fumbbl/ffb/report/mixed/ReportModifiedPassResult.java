package com.fumbbl.ffb.report.mixed;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.NoDiceReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportModifiedPassResult extends NoDiceReport {

	private Skill fSkill;
	private PassResult passResult;

	public ReportModifiedPassResult() {
		super();
	}

	public ReportModifiedPassResult(Skill fSkill, PassResult passResult) {
		this.fSkill = fSkill;
		this.passResult = passResult;
	}

	public ReportId getId() {
		return ReportId.MODIFIED_PASS_RESULT;
	}

	public Skill getSkill() {
		return fSkill;
	}

	public PassResult getPassResult() {
		return passResult;
	}

// transformation

	public IReport transform(IFactorySource source) {
		return new ReportModifiedPassResult(getSkill(), passResult);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.SKILL.addTo(jsonObject, fSkill);
		IJsonOption.PASS_RESULT.addTo(jsonObject, passResult);
		return jsonObject;
	}

	public ReportModifiedPassResult initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fSkill = (Skill) IJsonOption.SKILL.getFrom(source, jsonObject);
		passResult = (PassResult) IJsonOption.PASS_RESULT.getFrom(source, jsonObject);
		return this;
	}

}
