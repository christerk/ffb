package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.ArrayList;
import java.util.List;

/**
 * Reports the optional roll modifier skills the coach decided to use.
 */
public class ClientCommandReRollModifierChoice extends ClientCommand {

	private final List<Skill> skills = new ArrayList<>();
	private String playerId;
	private ReRolledAction reRolledAction;

	public ClientCommandReRollModifierChoice() {
		super();
	}

	public ClientCommandReRollModifierChoice(String playerId, List<Skill> skills, ReRolledAction reRolledAction) {
		this.playerId = playerId;
		if (skills != null) {
			this.skills.addAll(skills);
		}
		this.reRolledAction = reRolledAction;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_RE_ROLL_MODIFIER_CHOICE;
	}

	public String getPlayerId() {
		return playerId;
	}

	public List<Skill> getSkills() {
		return skills;
	}

	public ReRolledAction getReRolledAction() {
		return reRolledAction;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		JsonArray skillArray = new JsonArray();
		for (Skill skill : skills) {
			skillArray.add(UtilJson.toJsonValue(skill));
		}
		IJsonOption.SKILL_ARRAY.addTo(jsonObject, skillArray);
		IJsonOption.RE_ROLLED_ACTION.addTo(jsonObject, reRolledAction);
		return jsonObject;
	}

	public ClientCommandReRollModifierChoice initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		skills.clear();
		JsonArray skillArray = IJsonOption.SKILL_ARRAY.getFrom(source, jsonObject);
		if (skillArray != null) {
			SkillFactory skillFactory = source.getFactory(FactoryType.Factory.SKILL);
			for (int i = 0; i < skillArray.size(); i++) {
				skills.add((Skill) UtilJson.toEnumWithName(skillFactory, skillArray.get(i)));
			}
		}
		if (IJsonOption.RE_ROLLED_ACTION.isDefinedIn(jsonObject)) {
			reRolledAction = (ReRolledAction) IJsonOption.RE_ROLLED_ACTION.getFrom(source, jsonObject);
		}
		return this;
	}
}
