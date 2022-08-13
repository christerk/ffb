package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.NoDiceReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportModifiedDodgeResultSuccessful extends NoDiceReport {

	private Skill fSkill;

	public ReportModifiedDodgeResultSuccessful() {
		super();
	}

	public ReportModifiedDodgeResultSuccessful(Skill fSkill) {
		this.fSkill = fSkill;
	}

	public ReportId getId() {
		return ReportId.MODIFIED_DODGE_RESULT_SUCCESSFUL;
	}

	public Skill getSkill() {
		return fSkill;
	}

// transformation

	public IReport transform(IFactorySource source) {
		return new ReportModifiedDodgeResultSuccessful(getSkill());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.SKILL.addTo(jsonObject, fSkill);
		return jsonObject;
	}

	public ReportModifiedDodgeResultSuccessful initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fSkill = (Skill) IJsonOption.SKILL.getFrom(source, jsonObject);
		return this;
	}

}
