package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Skill;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportSkillUse implements IReport {

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

	public IReport transform() {
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

	public ReportSkillUse initFrom(JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
		fSkill = (Skill) IJsonOption.SKILL.getFrom(jsonObject);
		fUsed = IJsonOption.USED.getFrom(jsonObject);
		fSkillUse = (SkillUse) IJsonOption.SKILL_USE.getFrom(jsonObject);
		return this;
	}

}
