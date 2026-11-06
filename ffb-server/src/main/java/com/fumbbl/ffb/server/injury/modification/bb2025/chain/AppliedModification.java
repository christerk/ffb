package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.IServerJsonOption;

/**
 * A skill applied on the path from the root to a node.
 */
public class AppliedModification implements IJsonSerializable {

	private Skill skill;
	private String playerId;
	private InjuryPhase phase;
	private SkillUse skillUse;

	public AppliedModification() {
	}

	public AppliedModification(Skill skill, String playerId, InjuryPhase phase, SkillUse skillUse) {
		this.skill = skill;
		this.playerId = playerId;
		this.phase = phase;
		this.skillUse = skillUse;
	}

	public Skill getSkill() {
		return skill;
	}

	public String getPlayerId() {
		return playerId;
	}

	public InjuryPhase getPhase() {
		return phase;
	}

	public SkillUse getSkillUse() {
		return skillUse;
	}

	public boolean matches(Skill otherSkill, String otherPlayerId) {
		return skill == otherSkill && playerId != null && playerId.equals(otherPlayerId);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.SKILL.addTo(jsonObject, skill);
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IServerJsonOption.INJURY_PHASE.addTo(jsonObject, phase.name());
		IServerJsonOption.SKILL_USE.addTo(jsonObject, skillUse);
		return jsonObject;
	}

	@Override
	public AppliedModification initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		skill = (Skill) IServerJsonOption.SKILL.getFrom(source, jsonObject);
		playerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		phase = InjuryPhase.valueOf(IServerJsonOption.INJURY_PHASE.getFrom(source, jsonObject));
		skillUse = (SkillUse) IServerJsonOption.SKILL_USE.getFrom(source, jsonObject);
		return this;
	}
}
