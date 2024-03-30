package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportSkillUse extends NoDiceReport {

	private String fPlayerId;
	private Skill fSkill;
	private boolean fUsed;
	private SkillUse fSkillUse;

	public ReportSkillUse() {
		super();
	}

	public ReportSkillUse(Skill pSkill, boolean pUsed, SkillUse pSkillUse) {
		this(null, pSkill, pUsed, pSkillUse);
	}

	public ReportSkillUse(String pPlayerId, Skill pSkill, boolean pUsed, SkillUse pSkillUse) {
		fPlayerId = pPlayerId;
		fSkill = pSkill;
		fUsed = pUsed;
		fSkillUse = pSkillUse;
	}

	public ReportId getId() {
		return ReportId.SKILL_USE;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public Skill getSkill() {
		return fSkill;
	}

	public boolean isUsed() {
		return fUsed;
	}

	public SkillUse getSkillUse() {
		return fSkillUse;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportSkillUse(getPlayerId(), getSkill(), isUsed(), getSkillUse());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.SKILL.addTo(jsonObject, fSkill);
		IJsonOption.USED.addTo(jsonObject, fUsed);
		IJsonOption.SKILL_USE.addTo(jsonObject, fSkillUse);
		return jsonObject;
	}

	public ReportSkillUse initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fSkill = (Skill) IJsonOption.SKILL.getFrom(source, jsonObject);
		fUsed = IJsonOption.USED.getFrom(source, jsonObject);
		fSkillUse = (SkillUse) IJsonOption.SKILL_USE.getFrom(source, jsonObject);
		return this;
	}

}
