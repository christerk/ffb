package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.ArrayList;
import java.util.List;

/**
 * Reports the armour / injury modification option chosen by the coach. An empty skill list declines all options.
 */
public class ClientCommandInjuryModificationChoice extends ClientCommand {

	private final List<Skill> skills = new ArrayList<>();

	public ClientCommandInjuryModificationChoice() {
		super();
	}

	public ClientCommandInjuryModificationChoice(List<Skill> skills) {
		if (skills != null) {
			this.skills.addAll(skills);
		}
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_INJURY_MODIFICATION_CHOICE;
	}

	public List<Skill> getSkills() {
		return skills;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		JsonArray skillArray = new JsonArray();
		for (Skill skill : skills) {
			skillArray.add(UtilJson.toJsonValue(skill));
		}
		IJsonOption.SKILL_ARRAY.addTo(jsonObject, skillArray);
		return jsonObject;
	}

	public ClientCommandInjuryModificationChoice initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		skills.clear();
		JsonArray skillArray = IJsonOption.SKILL_ARRAY.getFrom(source, jsonObject);
		if (skillArray != null) {
			SkillFactory skillFactory = source.getFactory(FactoryType.Factory.SKILL);
			for (int i = 0; i < skillArray.size(); i++) {
				skills.add((Skill) UtilJson.toEnumWithName(skillFactory, skillArray.get(i)));
			}
		}
		return this;
	}
}
