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
public class ReportSkillWasted extends NoDiceReport {

	private String fPlayerId;
	private Skill fSkill;

	public ReportSkillWasted() {
		super();
	}

	public ReportSkillWasted(Skill pSkill) {
		this(null, pSkill);
	}

	public ReportSkillWasted(String pPlayerId, Skill pSkill) {
		fPlayerId = pPlayerId;
		fSkill = pSkill;
	}

	public ReportId getId() {
		return ReportId.SKILL_WASTED;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public Skill getSkill() {
		return fSkill;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportSkillWasted(getPlayerId(), getSkill());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.SKILL.addTo(jsonObject, fSkill);
		return jsonObject;
	}

	public ReportSkillWasted initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fSkill = (Skill) IJsonOption.SKILL.getFrom(source, jsonObject);
		return this;
	}

}
