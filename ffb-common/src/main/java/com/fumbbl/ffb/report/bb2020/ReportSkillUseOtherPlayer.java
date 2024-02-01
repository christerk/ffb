package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.NoDiceReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportSkillUseOtherPlayer extends NoDiceReport {

	private String playerId, otherPlayerId;
	private Skill skill;
	private SkillUse skillUse;

	public ReportSkillUseOtherPlayer() {
		super();
	}


	public ReportSkillUseOtherPlayer(String playerId, Skill skill, SkillUse skillUse, String otherPlayerId) {
		this.playerId = playerId;
		this.otherPlayerId = otherPlayerId;
		this.skill = skill;
		this.skillUse = skillUse;
	}

	public ReportId getId() {
		return ReportId.SKILL_USE_OTHER_PLAYER;
	}

	public String getPlayerId() {
		return playerId;
	}

	public Skill getSkill() {
		return skill;
	}

	public SkillUse getSkillUse() {
		return skillUse;
	}

	public String getOtherPlayerId() {
		return otherPlayerId;
	}

// transformation

	public IReport transform(IFactorySource source) {
		return new ReportSkillUseOtherPlayer(playerId, skill, skillUse, otherPlayerId);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.PLAYER_ID_OTHER_PLAYER.addTo(jsonObject, otherPlayerId);
		IJsonOption.SKILL.addTo(jsonObject, skill);
		IJsonOption.SKILL_USE.addTo(jsonObject, skillUse);
		return jsonObject;
	}

	public ReportSkillUseOtherPlayer initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		skill = (Skill) IJsonOption.SKILL.getFrom(source, jsonObject);
		skillUse = (SkillUse) IJsonOption.SKILL_USE.getFrom(source, jsonObject);
		otherPlayerId = IJsonOption.PLAYER_ID_OTHER_PLAYER.getFrom(source, jsonObject);
		return this;
	}

}
