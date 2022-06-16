package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * @author Kalimar
 */
public class ClientCommandUseSkill extends ClientCommand {

	private Skill fSkill;
	private boolean fSkillUsed, neverUse;
	private String playerId;
	private ReRolledAction reRolledAction;

	public ClientCommandUseSkill() {
		super();
	}

	public ClientCommandUseSkill(Skill pSkill, boolean pSkillUsed, String playerId, ReRolledAction reRolledAction, boolean neverUse) {
		fSkill = pSkill;
		fSkillUsed = pSkillUsed;
		this.playerId = playerId;
		this.reRolledAction = reRolledAction;
		this.neverUse = neverUse;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_USE_SKILL;
	}

	public boolean isSkillUsed() {
		return fSkillUsed;
	}

	public Skill getSkill() {
		return fSkill;
	}

	public String getPlayerId() {
		return playerId;
	}

	public ReRolledAction getReRolledAction() {
		return reRolledAction;
	}

	public boolean isNeverUse() {
		return neverUse;
	}
	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.SKILL.addTo(jsonObject, fSkill);
		IJsonOption.SKILL_USED.addTo(jsonObject, fSkillUsed);
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.RE_ROLLED_ACTION.addTo(jsonObject, reRolledAction);
		IJsonOption.SKILL_NEVER_USE.addTo(jsonObject, neverUse);
		return jsonObject;
	}

	public ClientCommandUseSkill initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fSkill = (Skill) IJsonOption.SKILL.getFrom(source, jsonObject);
		fSkillUsed = IJsonOption.SKILL_USED.getFrom(source, jsonObject);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		if (IJsonOption.RE_ROLLED_ACTION.isDefinedIn(jsonObject)) {
			reRolledAction = (ReRolledAction) IJsonOption.RE_ROLLED_ACTION.getFrom(source, jsonObject);
		}
		if (IJsonOption.SKILL_NEVER_USE.isDefinedIn(jsonObject)) {
			neverUse = IJsonOption.SKILL_NEVER_USE.getFrom(source, jsonObject);
		}
		return this;
	}
}
